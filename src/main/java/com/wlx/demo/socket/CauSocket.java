package com.wlx.demo.socket;

import java.io.*;
import java.net.Socket;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class CauSocket {
    private static final byte[] BYTE_GET = new byte[]{103, 101, 116, 32};
    private static final byte[] BYTE_SET = new byte[]{115, 101, 116, 32};
    private static final byte[] BYTE_CRLF = new byte[]{13, 10};
    private static final byte[] BYTE_DELETE = new byte[]{100, 101, 108, 101, 116, 101, 32};
    private static final byte[] BYTE_SPACE = new byte[]{32};
    private static final int COMPRESS_THRESHOLD = 102400;
    private static final int MAX_BYTE_SIZE = 5242880;

    public static void main(String[] args) throws Exception {
        Socket socket = new Socket("10.201.0.4", 13333);

        // 设
//        setCauKeyValue("B931100012865", "931", socket);

        // 取
        String[] keys = new String[]{"B16658352832", "U311093338156333", "B931100012865", "U311103338200433", "B931100002893", "U311103338210033"};

        for (String key : keys) {
            System.out.println(key + " = " + getCauValueByKey(key, socket));
        }
        socket.close();
    }

    private static void setCauKeyValue(String key, Object value, Socket socket) throws Exception {
        BufferedInputStream in = new BufferedInputStream(socket.getInputStream());
        BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());
        int flag = 0;
        byte[] bs = value.toString().getBytes();

        if (bs.length > COMPRESS_THRESHOLD) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream(bs.length);
            GZIPOutputStream gos = new GZIPOutputStream(bos);
            gos.write(bs, 0, bs.length);
            gos.finish();
            bs = bos.toByteArray();
            flag |= 2;
        }
        if (bs.length >= MAX_BYTE_SIZE) {
            throw new Exception("不能超过" + MAX_BYTE_SIZE + "字节");
        }
        out.write(BYTE_SET);
        out.write(key.getBytes());
        out.write(BYTE_SPACE);
        out.write(String.valueOf(flag).getBytes());
        out.write(BYTE_SPACE);
        out.write("0".getBytes());
        out.write(BYTE_SPACE);
        out.write(String.valueOf(bs.length).getBytes());
        out.write(BYTE_CRLF);
        out.write(bs);
        out.write(BYTE_CRLF);
        out.flush();
        String ret = readLine(in);
        boolean rtn = "STORED".equals(ret);
        if (!rtn) {
            throw new Exception("set出现错误:" + ret);
        }
    }

    private static Object getCauValueByKey(String key, Socket socket) throws Exception {
        BufferedInputStream in = new BufferedInputStream(socket.getInputStream());
        BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());

        out.write(BYTE_GET);
        out.write(key.getBytes());
        out.write(BYTE_CRLF);

        try {
            out.flush();
        } catch (Exception e) {
            throw e;
        }
        return getObjectFromStream(in);
    }


    private static Object getObjectFromStream(InputStream in) throws NullPointerException, IOException {
        String cmd = readLine(in);

        if (cmd == null) {
            throw new NullPointerException("读取命令出现返回空指针");
        } else if (!cmd.startsWith("VALUE")) {
            return null;
        } else {
            String[] part = cmd.split(" ");
            int flag = Integer.parseInt(part[2]);
            int length = Integer.parseInt(part[3]);
            byte[] bs = new byte[length];

            int count;
            for(count = 0; count < bs.length; count += in.read(bs, count, bs.length - count)) {
                ;
            }
            if (count != bs.length) {
                throw new IOException("读取数据长度错误");
            } else {
                readLine(in);
                String endstr = readLine(in);
                if (!"END".equals(endstr)) {
                    throw new IOException("结束标记错误");
                } else {
                    if ((flag & 2) != 0) {
                        GZIPInputStream gzi = new GZIPInputStream(new ByteArrayInputStream(bs));
                        ByteArrayOutputStream bos = new ByteArrayOutputStream(bs.length);
                        int i;
                        byte[] tmp = new byte[2048];

                        while((i = gzi.read(tmp)) != -1) {
                            bos.write(tmp, 0, i);
                        }

                        bs = bos.toByteArray();
                        gzi.close();
                    }

                    return new String(bs);
                }
            }
        }
    }

    private static String readLine(InputStream in) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        boolean eol = false;

        for(byte[] b = new byte[1]; in.read(b, 0, 1) != -1; bos.write(b, 0, 1)) {
            if (b[0] == 13) {
                eol = true;
            } else {
                if (eol && b[0] == 10) {
                    break;
                }

                eol = false;
            }
        }

        return bos.size() == 0 ? null : bos.toString().trim();
    }
}
