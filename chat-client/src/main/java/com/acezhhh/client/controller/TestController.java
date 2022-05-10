package com.acezhhh.client.controller;

import com.acezhhh.client.config.RandomTxtConfig;
import com.acezhhh.client.netty.NettyClient;
import com.acezhhh.common.enums.MessageTypeEnum;
import com.acezhhh.common.vo.ChatVo;
import com.acezhhh.common.vo.MessageVo;
import com.acezhhh.common.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

/**
 * 模拟多人群聊
 * @author acezhhh
 * @date 2022/2/2
 */

@RestController
@RequestMapping(value = "/test")
public class TestController {

    @Autowired
    private RandomTxtConfig randomTxtConfig;

    private List<NettyClient> nettyClientList = new ArrayList<>();

    @GetMapping("register")
    public String test() throws InterruptedException {
        NettyClient nettyClient = new NettyClient();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        new Thread(() -> nettyClient.start(countDownLatch)).start();
        countDownLatch.await();
        registerUser(nettyClient);
        nettyClientList.add(nettyClient);
        return "register success";
    }

    @GetMapping("chat")
    public String chat() {
        new Thread(()->{
            Random ra = new Random();
            while (true){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (nettyClientList.size() == 0){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("暂无用户连接");
                    continue;
                }
                int index = ra.nextInt(nettyClientList.size());
                NettyClient nettyClient = nettyClientList.get(index);
                this.sendMsg(nettyClient);
            }
        }).start();
        return "chat";
    }


    private synchronized void registerUser(NettyClient nettyClient) throws InterruptedException {
        String head = "head" + (new Random().nextInt(24) + 1);
        UserVo userVo = UserVo.builder()
                .id(UUID.randomUUID().toString())
                .userName(randomTxtConfig.getName())
                .head(head)
                .build();
        System.out.println(userVo.toString());
        MessageVo messageVo = MessageVo.builder()
                .data(userVo)
                .type(MessageTypeEnum.USER_REGISTER)
                .build();
        nettyClient.sendMessage(messageVo);
    }

    private void sendMsg(NettyClient nettyClient) {
        ChatVo chatVo = ChatVo.builder()
                .content(randomTxtConfig.getSentence())
                .targetChannelId("ALL")
                .build();
        MessageVo messageVo = MessageVo.builder()
                .data(chatVo)
                .type(MessageTypeEnum.CHAT)
                .build();
        nettyClient.sendMessage(messageVo);
    }

}
