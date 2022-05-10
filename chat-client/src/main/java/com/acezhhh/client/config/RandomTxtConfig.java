package com.acezhhh.client.config;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author caezhhh
 * @date 2022/5/10
 */
@Component
public class RandomTxtConfig {

    private static List<String> txtList;
    private static List<String> nameList;

    @PostConstruct
    private void initTxt(){
        nameList = readTxtToObject("name.txt");
        txtList = readTxtToObject("sentence.txt");
    }

    /**
     * 获取随机名称
     * @return
     */
    public String getName(){
        return nameList.get((int) (Math.random() * nameList.size()));
    }

    /**
     * 获取输入内容
     * @return
     */
    public String getSentence(){
        return txtList.get((int) (Math.random() * txtList.size()));
    }

    private static List<String> readTxtToObject(String fileName) {
        List<String> txtList = new ArrayList<>();
        try {
            ClassPathResource classPathResource = new ClassPathResource("txt/" + fileName);
            InputStream inputStream =classPathResource.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String lineTxt = null;
            // 逐行读取
            while ((lineTxt = br.readLine()) != null) {
                txtList.add(lineTxt);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return txtList;
    }


}
