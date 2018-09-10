package com.kenhome.config.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

/**
 * @Description:消费者
 * @author: cmk
 * @date: 2018年6月9日 上午11:33:24
 */

@Component
public class Receiver3 {
    private static final Logger logger = LoggerFactory.getLogger(Receiver3.class);


    public void receiveMessage3(String message) {

        logger.info("receiver3接收到的数据 <" + message + ">");

        int size = 200;

        for (int i = 0; i < size; i++) {
            logger.info("..." + message);
        }

    }

}