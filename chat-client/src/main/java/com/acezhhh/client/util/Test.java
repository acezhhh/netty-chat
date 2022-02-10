package com.acezhhh.client.util;

import java.util.Random;

/**
 * @author acezhhh
 * @date 2022/2/9
 */
public class Test {

    public static void main(String[] args) {
        Random random = new Random();
        String head = "head" + new Random().nextInt(24) + 1;
        for (int i = 0; i < 100; i++) {
            System.out.println("head" + (new Random().nextInt(24) + 1));
        }
    }
}
