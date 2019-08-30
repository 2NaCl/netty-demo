package com.fyj.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyServerDemo {

    public static void main(String[] args) throws InterruptedException {
        final EchoServerHandler serverHandler
                = new EchoServerHandler();
        //boss线程组
        EventLoopGroup bossGroup
                = new NioEventLoopGroup(1);
        //worker线程组
        EventLoopGroup workerGroup
                = new NioEventLoopGroup();

        try{
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(serverHandler);
                        }
                    });
            //bind 服务端端口
            ChannelFuture future = b.bind(9090).sync();
            future.channel().closeFuture().sync();
        }finally {
            //终止工作线程组
            workerGroup.shutdownGracefully();
            //终止工作线程组
            bossGroup.shutdownGracefully();
        }
    }
}


//socket连接处理器
class EchoServerHandler extends ChannelInboundHandlerAdapter{

    //处理读事件
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ctx.write(msg);
    }
    //处理读完成事件
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }
    //处理异常事件
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
