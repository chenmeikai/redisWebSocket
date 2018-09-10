package com.kenhome.config.redis;

import com.kenhome.config.webSocket.WebSocketServer;
import com.kenhome.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @Description:消费者
 * @author: cmk
 * @date: 2018年6月9日 上午11:33:24
 */
public class Receiver {
    private static final Logger logger = LoggerFactory.getLogger(Receiver.class);

    private CountDownLatch latch;

    public void receiveMessage(String message) {

        int last = message.lastIndexOf("\"");
        message = message.substring(1, last).replace("\\", "");

        logger.info("receiver接收到的数据 <" + message + ">");

        latch.countDown();
    }

    public CountDownLatch getLatch() {
        return latch;
    }

    public void setLatch(CountDownLatch latch) {
        this.latch = latch;
    }
}