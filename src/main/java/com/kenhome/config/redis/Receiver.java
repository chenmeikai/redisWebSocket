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
 * @date:   2018年6月9日 上午11:33:24
 */
public class Receiver {
    private static final Logger logger = LoggerFactory.getLogger(Receiver.class);

    private CountDownLatch latch;

    @Autowired
    public Receiver(CountDownLatch latch) {
        this.latch = latch;
    }

    public void receiveMessage(String message) {
        
         int last =message.lastIndexOf("\"");
         message =message.substring(1,last).replace("\\", "");
         
         logger.info("接收到的数据 <" + message + ">");
        
        Long userId = JsonUtils.getLong(message, "userId");
        
        WebSocketServer webSocketServer  =  WebSocketServer.webSocketMap.get(userId);
        
        try {
			webSocketServer.sendMessage(userId+"处理的结果 <成功>");
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        logger.info(userId+"处理的结果 <成功>");
        
        latch.countDown();
    }
}