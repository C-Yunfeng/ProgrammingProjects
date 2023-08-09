package com.itheima.test;

import org.junit.jupiter.api.Test;

public class SendSmsTest {
    @Test
    public void test1(){
        String fileName= "xx.jpg";
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        System.out.println(suffix);
    }
}
