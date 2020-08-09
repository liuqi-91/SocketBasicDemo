package com.lx.socketbasicdemo;

import java.nio.charset.Charset;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyServerBoot {

    public static final String HOST = "127.0.0.1";
    public static final int PORT = 8002;
    private final int port;

    public NettyServerBoot(int port) throws Exception{
        this.port = port;
    }

    public static void main(String[] args) throws Exception{
        new NettyServerBoot(PORT).startNettyServerBoot();
    }

    private void startNettyServerBoot() throws Exception{

        ServerBootstrap bootstrap = new ServerBootstrap();

        NioEventLoopGroup boss = new NioEventLoopGroup(1);
        NioEventLoopGroup work = new NioEventLoopGroup();

        bootstrap.group(boss, work)
                .channel(NioServerSocketChannel.class)
                .childOption(ChannelOption.TCP_NODELAY,true)
                .handler(new ServerHandler(true))
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ServerHandler(false));
                    }
                });
        try {
            ChannelFuture future = bootstrap.bind(port).sync();
            System.out.println(String.format("NettyServerBoot bind %s success, server channel = %s",port,future.channel()));
            future.channel().closeFuture().sync();
        } finally {
            bootstrap.group().shutdownGracefully().sync();
            bootstrap.childGroup().shutdownGracefully().sync();
        }
    }

    public class ServerHandler extends ChannelInboundHandlerAdapter{

        public boolean isAccept ;

        public ServerHandler(boolean isAccept) {
            this.isAccept = isAccept;
        }

        @Override
        public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
            System.out.println("ctx socket channelRegistered = "+ctx.channel());
            super.channelRegistered(ctx);
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("ctx socket channelActive = "+ctx.channel());
            super.channelActive(ctx);
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if(!isAccept) {
                ByteBuf byteBuf = (ByteBuf) msg;
                System.out.println("ctx = " + ctx.channel() + "\nserver receive message = " + byteBuf.toString(Charset.defaultCharset()));
                Thread.sleep(5000);
                ctx.writeAndFlush(msg);
            } else {
                System.out.println("ctx socket is Accept ctx = "+ctx.channel());
                super.channelRead(ctx,msg);
            }
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            System.out.println("ctx socket channelReadComplete = "+ctx.channel());
            super.channelReadComplete(ctx);
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("ctx socket channelInactive = "+ctx.channel());
            super.channelInactive(ctx);
        }

        @Override
        public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
            System.out.println("ctx socket channelUnregistered = "+ctx.channel());
            super.channelUnregistered(ctx);
        }
    }
}
