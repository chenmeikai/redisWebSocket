/**
 * Copyright © 2018
 *
 * @Package: RedisController.java
 * @author: Administrator
 * @date: 2018年6月9日 下午5:26:35
 */
package com.kenhome.controller;

import com.kenhome.config.redis.RedisClient;
import com.kenhome.config.redis.RedisLock;
import com.kenhome.service.LockTestService;
import com.kenhome.service.SecKillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.UUID;

/**
 * @Description:秒杀商品
 * @author: cmk
 * @date: 2018年7月3日 下午9:12:35
 */

@RestController
public class SecKillController {


    @Autowired
    RedisClient redisClient;

    @Resource
    SecKillService secKillService;

    private final String SECKILLNUM = "aecKillNum";


    @GetMapping("buy")
    public String SecKill(String key) throws  Exception {

       String result ="已经被抢购完啦";

       int resultCode = secKillService.secKill(SECKILLNUM);
       if(resultCode ==0){
          return result;
       }
       if(resultCode ==1){
           result="恭喜，抢购成功了！";
           return  result;
       }
       if (resultCode ==2){
           result="抢购失败，请重试.";
           return  result;
       }
        return result;
    }



    @GetMapping("initProduct")
    public String init(int num) {

        try {
            redisClient.setObject(SECKILLNUM, 10);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "well done";
    }


}
