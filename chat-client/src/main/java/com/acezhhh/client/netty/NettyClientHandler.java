package com.acezhhh.client.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author acezhhh
 * @date 2022/2/2
 */
public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    private String result;

    @Override

    public void channelActive(ChannelHandlerContext ctx) {
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        //if (!ObjectUtils.isEmpty(msg)) {
        result = (String) msg;
        //}
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}