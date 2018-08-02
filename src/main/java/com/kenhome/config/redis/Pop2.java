package com.kenhome.config.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @Author: cmk
 * @Description:
 * @Date: 2018\8\2 0002 21:10
 */

public class Pop2 implements CommandLineRunner {

    @Autowired
    private RedisClient redisClient;

    private static final Logger log =LoggerFactory.getLogger(Pop2.class);


    @Override
    public void run(String... strings) throws Exception {

        System.out.println("启动2");

        while (true){

            String message =null;

            try {
                //阻塞获取message
                message = redisClient.rightPopBlock("demo",30L,TimeUnit.SECONDS);

            } catch (Exception e) {
                log.info("如果redis出错，则休眠5秒，再继续循环");
                e.printStackTrace();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                continue;
            }

            try {
                System.out.println("pop2获得的信息："+message+",为空则进入下个循环");
                if(message==null){
                    continue;
                }
                //TODO 业务处理 捕捉异常，防止跳出循环
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }


}
