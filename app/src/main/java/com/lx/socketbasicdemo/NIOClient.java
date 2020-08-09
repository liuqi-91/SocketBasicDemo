package com.lx.socketbasicdemo;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NIOClient {

    private final String host;
    private final int port;
    public NIOClient(String host, int port) throws Exception {
        this.host = host;
        this.port = port;
    }

    private void startClient() throws Exception{

        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);

        Selector selector = Selector.open();
        socketChannel.register(selector, SelectionKey.OP_CONNECT);
        socketChannel.connect(new InetSocketAddress(host,port));
        while(true){
            try{
                selector.select(1000);
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> it = selectionKeys.iterator();
                while(it.hasNext()){
                    SelectionKey key = it.next();
                    if (key.isConnectable() && ((SocketChannel)(key.channel())).finishConnect()) {
                        int ops = key.interestOps();
                        ops &= ~SelectionKey.OP_CONNECT;
                        key.interestOps(ops);
                        ((SocketChannel)(key.channel())).register(selector,SelectionKey.OP_READ);

                        byte[] bytes = "hello NIOServer".getBytes();
                        ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
                        writeBuffer.put(bytes);
                        writeBuffer.flip();
                        ((SocketChannel)(key.channel())).write(writeBuffer);
                        System.out.println("Send order 2 server succeed.");
                    }
                    if (key.isReadable()) {
                        ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                        int readBytes = ((SocketChannel)(key.channel())).read(readBuffer);
                        if (readBytes > 0) {
                            readBuffer.flip();
                            byte[] bytes = new byte[readBuffer.remaining()];
                            readBuffer.get(bytes);
                            String body = new String(bytes, "UTF-8");
                            System.out.println("receive server message " + body);
                        }
                    }

                    it.remove();
                }
            }catch (Exception e){
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

    public static void main(String[] args) throws Exception{

        new NIOClient(NIOServerBoot.HOST, NIOServerBoot.PORT).startClient();

    }
}
