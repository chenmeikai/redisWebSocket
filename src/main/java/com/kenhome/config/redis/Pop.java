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
@Component
@Order(value = 1)
public class Pop implements CommandLineRunner {

    @Autowired
    private RedisClient redisClient;

    private static final Logger log = LoggerFactory.getLogger(Pop.class);


    @Override
    public void run(String... strings) throws Exception {

        Receive1 receive1 = new Receive1(redisClient);

        Receive2 receive2 = new Receive2(redisClient);

        Thread thread1 = new Thread(receive1);

        Thread thread2 = new Thread(receive2);

        thread1.start();
        thread2.start();

    }
}


class Receive1 implements Runnable {

    private RedisClient redisClient;

    public Receive1(RedisClient redisClient) {
        this.redisClient = redisClient;
    }

    @Override
    public void run() {

        System.out.println("启动1");

        while (true) {

            String message = null;

            try {
                //阻塞获取message
                message = redisClient.rightPopBlock("demo", 30L, TimeUnit.SECONDS);


            } catch (Exception e) {
                System.out.println("如果redis出错，则休眠5秒，再继续循环");
                e.printStackTrace();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                continue;
            }


            try {
                System.out.println("pop获得的信息：" + message + ",为空则进入下个循环");
                if (message == null) {
                    continue;
                }
                //TODO 业务处理 捕捉异常，防止跳出循环
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }
}


class Receive2 implements Runnable {

    private RedisClient redisClient;

    public Receive2(RedisClient redisClient) {
        this.redisClient = redisClient;
    }

    @Override
    public void run() {

        System.out.println("启动2");

        while (true) {

            String message = null;

            try {
                //阻塞获取message
                message = redisClient.rightPopBlock("demo", 30L, TimeUnit.SECONDS);


            } catch (Exception e) {
                System.out.println("如果redis出错，则休眠5秒，再继续循环");
                e.printStackTrace();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                continue;
            }


            try {
                System.out.println("pop2获得的信息：" + message + ",为空则进入下个循环");
                if (message == null) {
                    continue;
                }
                //TODO 业务处理 捕捉异常，防止跳出循环
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }
}

