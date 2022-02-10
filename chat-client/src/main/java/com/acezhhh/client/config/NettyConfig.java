package com.acezhhh.client.config;

import com.acezhhh.client.netty.NettyClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * @author acezhhh
 * @date 2022/2/8
 */
@Component
public class NettyConfig {

    private List<NettyClient> nettyClientList = new ArrayList<>();

    public void addClient(NettyClient nettyClient){
        nettyClientList.add(nettyClient);
    }

}
