package com.lx.socketbasicdemo;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * Java NIO 3大核心：
 * - Channel
 * - Selector
 * - Buffer
 */
public class NIOServerBoot {

    public static final String HOST = "127.0.0.1";
    public static final int PORT = 8001;

    private final int port;

    public NIOServerBoot(int port){
        this.port = port;
    }

    public static void main(String[] args) throws Exception{
        new NIOServerBoot(PORT).startServerBoot();
    }

    private void startServerBoot() throws Exception{
        try {
            final ServerSocketChannel server = ServerSocketChannel.open();
            server.configureBlocking(false);
            server.socket().bind(new InetSocketAddress(port));

            final Selector selector = Selector.open();
            server.register(selector, SelectionKey.OP_ACCEPT);

            System.out.println(String.format("服务端启动，绑定并开始监听%s端口, ServerSocket = %s",PORT,server));
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while(true){
                            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                            while(iterator.hasNext()){
                                SelectionKey selectionKey = iterator.next();
                                if (selectionKey.isAcceptable()) {
                                    handleAccept(selectionKey);
                                } else if(selectionKey.isReadable()){
                                    handleRead(selectionKey);
                                } else if(selectionKey.isWritable() && selectionKey.isValid()){
                                    handleWrite(selectionKey);
                                } else if (selectionKey.isConnectable()) {
                                    System.out.println("连接还在");
                                }
                                iterator.remove();
                            }
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

    private void handleAccept(SelectionKey key) throws Exception{
        SocketChannel socketChannel = ((ServerSocketChannel)(key.channel())).accept();
        socketChannel.configureBlocking(false);

        socketChannel.register(key.selector(),SelectionKey.OP_READ);
        System.out.println("来了一条连接, 让接入的连接开始， socketChannel = " + socketChannel);

    }

    private void handleRead(SelectionKey key) {
        // TODO: LQ 2020/8/9 处理读事件 
    }

    private void handleWrite(SelectionKey key) {
        // TODO: LQ 2020/8/9 处理写事件
    }

}
