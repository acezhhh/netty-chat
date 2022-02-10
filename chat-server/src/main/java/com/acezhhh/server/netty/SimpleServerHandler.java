package com.acezhhh.server.netty;

import com.acezhhh.common.vo.MessageVo;
import com.acezhhh.server.channel.UserChannel;
import com.acezhhh.server.service.MessageService;
import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author acezhhh
 * @date 2022/2/3
 */

@Component
@ChannelHandler.Sharable
public class SimpleServerHandler extends ChannelInboundHandlerAdapter {

    @Autowired
    private MessageService messageService;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        MessageVo messageVo = JSON.parseObject(msg.toString(), MessageVo.class);
        messageService.doService(ctx.channel(), messageVo);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        UserChannel userChannel = new UserChannel();
        userChannel.setChannel(ctx.channel());
        messageService.addChannel(userChannel);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        messageService.removeUser(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        messageService.removeUser(ctx.channel());
    }

}
