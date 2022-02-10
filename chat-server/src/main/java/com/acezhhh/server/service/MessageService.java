package com.acezhhh.server.service;

import com.acezhhh.common.enums.RequestEnum;
import com.acezhhh.common.vo.ChatVo;
import com.acezhhh.common.vo.MessageVo;
import com.acezhhh.common.vo.RequestVo;
import com.acezhhh.common.vo.UserVo;
import com.acezhhh.server.channel.UserChannel;
import com.alibaba.fastjson.JSON;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author acezhhh
 * @date 2022/1/30
 */
@Service
public class MessageService {

    //群
    private static final String groupId = "ALL";
    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    //用户和Channel
    private static ConcurrentHashMap<String, Channel> channelMap = new ConcurrentHashMap<>();

    public void doService(Channel channel, MessageVo messageVo) {
        String jsonStr = JSON.toJSONString(messageVo.getData());
        switch (messageVo.getType()) {
            case USER_REGISTER:
                UserVo userVo = JSON.parseObject(jsonStr, UserVo.class);
                this.userRegister(channel, userVo);
                break;
            case CHAT:
                ChatVo chatVo = JSON.parseObject(jsonStr, ChatVo.class);
                this.chat(channel, chatVo);
                break;
            default:
        }
    }

    /**
     * 用户注册到map并回传用户列表
     *
     * @param channel
     * @param userVo
     */
    private void userRegister(Channel channel, UserVo userVo) {
        System.out.println("---用户" + userVo.getHead() + "加入聊天---,当前人数:" + channelGroup.size() + "map:" + channelMap.size());
        UserChannel userChannel = (UserChannel) channelGroup.find(channel.id());
        userVo.setChannelId(channel.id().asLongText());
        userChannel.setUserVo(userVo);
        channelMap.put(channel.id().asLongText(), userChannel);
        this.addUser(userChannel);
    }

    /**
     * 添加用户发起通知
     */
    private void addUser(UserChannel userChannel) {
        RequestVo<UserVo> requestVo = new RequestVo<>(RequestEnum.ADD, userChannel.getUserVo());
        channelGroup.writeAndFlush(writeDataBuild(requestVo));
        channelGroup.forEach(channel -> {
            UserChannel otherUser = (UserChannel) channel;
            RequestVo<UserVo> request = new RequestVo<>(RequestEnum.ADD, otherUser.getUserVo());
            userChannel.writeAndFlush(writeDataBuild(request));
        });
    }

    /**
     * 聊天
     * @param channel
     * @param chatVo
     */
    public void chat(Channel channel, ChatVo chatVo) {
        UserChannel userChannel = (UserChannel) channelMap.get(channel.id().asLongText());
        UserVo currentUser = userChannel.getUserVo();

        String targetChannelId = chatVo.getTargetChannelId();
        if (groupId.equals(targetChannelId)) {
            this.sendGroup(currentUser, chatVo);
            return;
        }

        chatVo.setUserVo(currentUser);
        chatVo.setSourceChannelId(currentUser.getChannelId());
        RequestVo<ChatVo> requestVo = new RequestVo<>(RequestEnum.CHAT, chatVo);
        UserChannel targetUser = (UserChannel) channelMap.get(targetChannelId);
        targetUser.writeAndFlush(writeDataBuild(requestVo));

        //向自己的通道回写消息
        ChatVo myChatVo = new ChatVo();
        myChatVo.setUserVo(chatVo.getUserVo());
        myChatVo.setContent(chatVo.getContent());
        myChatVo.setSourceChannelId(targetUser.getChannel().id().asLongText());
        RequestVo<ChatVo> myRequestVo = new RequestVo<>(RequestEnum.CHAT, myChatVo);
        userChannel.writeAndFlush(writeDataBuild(myRequestVo));
    }

    private void sendGroup(UserVo currentUser, ChatVo chatVo) {
        chatVo.setSourceChannelId(groupId);
        chatVo.setUserVo(currentUser);
        RequestVo<ChatVo> requestVo = new RequestVo<>(RequestEnum.CHAT, chatVo);
        channelGroup.writeAndFlush(writeDataBuild(requestVo));
    }

    public void addChannel(UserChannel userChannel) {
        channelGroup.add(userChannel);
    }

    public void removeUser(Channel channel) {
        String longText = channel.id().asLongText();
        if(!channelMap.containsKey(longText)){
            return;
        }
        UserChannel userChannel = (UserChannel) channelMap.get(longText);
        channelMap.remove(longText);
        RequestVo<UserVo> RequestVo = new RequestVo<>(RequestEnum.REMOVE, userChannel.getUserVo());
        channelGroup.writeAndFlush(writeDataBuild(RequestVo));
    }

    private TextWebSocketFrame writeDataBuild(Object data) {
        return new TextWebSocketFrame(JSON.toJSONString(data));
    }


}
