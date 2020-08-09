package com.lx.socketbasicdemo;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
/**
 * Java传统 Socket特点
 * - inputStream.read() 按字节读取，无Buffer
 */
public class OIOServerBoot {

    public static final String HOST = "127.0.0.1";
    public static final int PORT = 8000;

    private final int port;

    public OIOServerBoot(int port){
        this.port = port;
    }

    public static void main(String[] args) throws Exception{
        new OIOServerBoot(PORT).startServerBoot();
    }

    private void startServerBoot() throws Exception{
        try {
            final ServerSocket server = new ServerSocket();
            server.bind(new InetSocketAddress(port));

            System.out.println(String.format("服务端启动，绑定并开始监听%s端口, ServerSocket = %s",PORT,server));
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while(true) {
                            Socket client = server.accept();
                            System.out.println("来了一条连接， socket = " + client);
                            new OIOClientHandler(client).start();
                        }
                    } catch (Exception e) {
                        System.out.println("服务端accept异常, e = " + e);
                    }
                }
            }).start();

        } catch (Exception e) {
            System.out.println("服务端启动异常");
        }
    }

    public class OIOClientHandler extends Thread{

        private Socket  client;

        public OIOClientHandler(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {
            try {
                InputStream inputStream = client.getInputStream();
                byte[] bytes = new byte[1024];
                int len = -1;
                while ((len = inputStream.read(bytes))!= -1) {
                    String message = new String(bytes, 0, len);
                    System.out.println("接收到客户端发来的消息 = "+message);
                    client.getOutputStream().write(bytes, 0, len);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}
