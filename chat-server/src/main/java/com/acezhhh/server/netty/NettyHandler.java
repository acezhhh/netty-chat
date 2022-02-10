package com.acezhhh.server.netty;

import com.acezhhh.common.vo.MessageVo;
import com.acezhhh.server.channel.UserChannel;
import com.acezhhh.server.service.MessageService;
import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * @author acezhhh
 * @date 2022/1/26
 */
@Component
@ChannelHandler.Sharable
public class NettyHandler extends SimpleChannelInboundHandler<Object> {

    @Autowired
    private MessageService messageService;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (msg instanceof WebSocketFrame) {
            System.out.println("--------WebSocketFrame----------");
            TextWebSocketFrame text = (TextWebSocketFrame) msg;
            MessageVo messageVo = JSON.parseObject(text.text(), MessageVo.class);
            messageService.doService(ctx.channel(), messageVo);
        }else{
            System.out.println("--------socket----------");
            System.out.println(msg.toString());
        }

    }

    /**
     * 客户端连接
     *
     * @param ctx
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        UserChannel userChannel = new UserChannel();
        userChannel.setChannel(ctx.channel());
        messageService.addChannel(userChannel);
    }

    /**
     * 客户端断开连接
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        messageService.removeUser(ctx.channel());
    }

}
