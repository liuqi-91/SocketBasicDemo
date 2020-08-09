package com.lx.socketbasicdemo;

import java.nio.charset.Charset;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyClient {
    private final String host;
    private final int port;

    public NettyClient(String host, int port) throws Exception {
        this.host = host;
        this.port = port;
    }

    public static void main(String[] args) throws Exception{

        new NettyClient(NettyServerBoot.HOST, NettyServerBoot.PORT).startNettyClient();
    }

    private void startNettyClient() throws Exception{

        Bootstrap bootstrap = new Bootstrap();
        NioEventLoopGroup work = new NioEventLoopGroup(1);
        bootstrap.group(work)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        System.out.println("NettyClient initChannel = "+ch);
                        ch.pipeline().addLast(new NettyClientHandler());
                    }
                });
        try {
            ChannelFuture future = bootstrap.connect(host, port).sync();
            System.out.println("NettyClient connect success, channel = " + future.channel());
            future.channel().closeFuture().sync();
        } finally {
            bootstrap.group().shutdownGracefully().sync();
        }
    }

    public class NettyClientHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
            System.out.println("NettyClientHandler channelRegistered = "+ctx.channel());
            super.channelRegistered(ctx);
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            ctx.writeAndFlush(Unpooled.copiedBuffer(("client say hello to server at "+System.currentTimeMillis()).getBytes()));

            System.out.println("NettyClientHandler channelActive and writeAndFlush = "+ctx.channel());
            super.channelActive(ctx);
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf byteBuf = (ByteBuf)msg;
            System.out.println("NettyClientHandler channelRead = "+ctx.channel()+"\nmsg = "+byteBuf.toString(Charset.defaultCharset()));
            Thread.sleep(5000);
            ctx.writeAndFlush(Unpooled.copiedBuffer(("client say hello to server at "+System.currentTimeMillis()).getBytes()));
            super.channelRead(ctx,msg);
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            System.out.println("NettyClientHandler channelReadComplete = "+ctx.channel());
            super.channelReadComplete(ctx);
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("NettyClientHandler channelInactive = "+ctx.channel());
            super.channelInactive(ctx);
        }

        @Override
        public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
            System.out.println("NettyClientHandler channelUnregistered = "+ctx.channel());
            super.channelUnregistered(ctx);
        }
    }
}
