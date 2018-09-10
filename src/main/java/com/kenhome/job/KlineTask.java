/**
 * Copyright © 2018
 *
 * @Package: cn.inesv.timer
 * @date: 2018年6月6日 上午11:36:35
 */

package com.kenhome.job;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArraySet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.kenhome.config.webSocket.KlineWebSocketServer;


/**
 * @Description:定时推送K线图数据
 * @author: cmk
 * @date: 2018年6月13日 上午10:34:10
 */

@Component
public class KlineTask {

    Logger logger = LoggerFactory.getLogger(KlineTask.class);


    @Scheduled(cron = "*/5 * * * * ?")
    public void send() {

        CopyOnWriteArraySet<KlineWebSocketServer> webSocketSet = KlineWebSocketServer.webSocketSet;

        try {

            //根据客户端请求类型定向推送
            for (KlineWebSocketServer server : webSocketSet) {


                String message = "{\"datas\":{\"data\":[[1528938120000,50.000000000000,50.000000000000,50.000000000000,50.000000000000,10.000000000000]]},\"success\":true}";
                String line = "[1528938120000,50.000000000000,50.000000000000,50.000000000000,50.000000000000,10.000000000000]";

                Random r = new Random();

                for (int i = 0; i < 10; i++) {

                    Thread.sleep(2000);
                    server.sendMessage(message);
                    Date date = new Date();
                    Long timeNum = date.getTime();


//            		line+=","+"["+timeNum+","+ (r.nextInt(301)+100)+","+ (r.nextInt(301)+100)+","+ (r.nextInt(301)+100)+","+ (r.nextInt(301)+100)+","+ (r.nextInt(301)+100)+"]";
                    line = "[" + timeNum + "," + (r.nextInt(301) + 100) + "," + (r.nextInt(301) + 100) + "," + (r.nextInt(301) + 100) + "," + (r.nextInt(301) + 100) + "," + (r.nextInt(301) + 100) + "]";


                    message = "{\"datas\":{\"data\":[" + line + "]},\"success\":true}";

                    System.out.println("k线图数据：" + message);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
