package com.kenhome.config.redis;

import jdk.nashorn.internal.objects.annotations.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.CountDownLatch;

/**
 * @Description:消费者
 * @author: cmk 
 * @date:   2018年6月9日 上午11:33:24
 */
public class Receiver2 {
    private static final Logger logger = LoggerFactory.getLogger(Receiver2.class);

    private CountDownLatch latch;

    public void receiveMessage2(String message) {

         logger.info("receiver2接收到的数据 <" + message + ">");
        logger.info("receiver2-count <" +  latch.getCount() + ">");

        for (int i=0;i<200;i++){
            System.out.println("...");
        }

        latch.countDown();
    }

    public CountDownLatch getLatch() {
        return latch;
    }
    public void setLatch(CountDownLatch latch) {
        this.latch = latch;
    }
}