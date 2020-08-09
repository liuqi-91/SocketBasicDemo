package com.lx.socketbasicdemo;

import java.io.InputStream;
import java.net.Socket;
import java.nio.charset.Charset;

public class OIOClient {

    private final String host;
    private final int port;
    public OIOClient(String host, int port) throws Exception {
        this.host = host;
        this.port = port;
    }

    private void startClient() throws Exception{
        final Socket socket = new Socket(host, port);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        socket.getOutputStream().write(("hello server at "+System.currentTimeMillis()).getBytes());
                        Thread.sleep(5000);
                    }
                } catch (Exception e) {

                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream inputStream = socket.getInputStream();
                    byte[] bytes = new byte[1024];
                    int len = -1;
                    while ((len = inputStream.read(bytes))!= -1) {
                        String message = new String(bytes, 0, len, Charset.defaultCharset());
                        System.out.println("接收到服务端返回的消息 = "+message);
                    }
                } catch (Exception e) {

                }
            }
        }).start();
    }

    public static void main(String[] args) throws Exception{

        new OIOClient(OIOServerBoot.HOST, OIOServerBoot.PORT).startClient();

    }
}
