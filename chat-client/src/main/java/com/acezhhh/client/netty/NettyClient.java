package com.acezhhh.client.netty;

import com.alibaba.fastjson.JSON;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

/**
 * @author acezhhh
 * @date 2022/2/2
 */

//@Component
public class NettyClient {

    static final String HOST = "localhost";
    static final int PORT = 8869;
    private EventLoopGroup group;
    private Bootstrap b;
    private ChannelFuture cf;
    private NettyClientInitializer nettyClientInitializer;


    public NettyClient() {
//        nettyClientInitializer = new NettyClientInitializer();
//        group = new NioEventLoopGroup();
//        b = new Bootstrap();
//        b.group(group)
//                .channel(NioSocketChannel.class)
//                .handler(nettyClientInitializer);
    }

    public void start(CountDownLatch countDownLatch) {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap()
                .group(group)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.SO_BACKLOG, 1024)// 配置TCP参数
                .option(ChannelOption.SO_BACKLOG, 1024) // 设置tcp缓冲区
                .option(ChannelOption.SO_SNDBUF, 32 * 1024) // 设置发送缓冲大小
                .option(ChannelOption.SO_RCVBUF, 32 * 1024) // 这是接收缓冲大小
                //该参数的作用就是禁止使用Nagle算法，使用于小数据即时传输
                .channel(NioSocketChannel.class)
                .handler(new NettyClientInitializer());
        try {
            cf = bootstrap.connect(HOST, PORT).sync();
            // 等待连接被关闭
            countDownLatch.countDown();
            cf.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            group.shutdownGracefully();
        }
    }

    public void connect() {
        try {
            this.cf = b.connect(HOST, PORT).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public ChannelFuture getChannelFuture() {
        if (this.cf == null) {
            this.connect();
        }
        if (!this.cf.channel().isActive()) {
            this.connect();
        }
        return this.cf;
    }

    public void close() {
        try {
            this.cf.channel().closeFuture().sync();
            this.group.shutdownGracefully();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(Object msg) {
        ChannelFuture cf = this.getChannelFuture();
        String text = JSON.toJSONString(msg) + "$_";
        cf.channel().writeAndFlush(Unpooled.copiedBuffer(text.getBytes()));
    }

}
