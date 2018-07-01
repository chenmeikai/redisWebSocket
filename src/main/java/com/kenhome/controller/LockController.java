/**
 * Copyright © 2018
 *
 * @Package: RedisController.java
 * @author: Administrator
 * @date: 2018年6月9日 下午5:26:35
 */
package com.kenhome.controller;

import com.kenhome.config.redis.RedisLock;
import com.kenhome.service.LockTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.UUID;

/**
 * @Description:TODO
 * @author: cmk
 * @date: 2018年6月9日 下午5:26:35
 */

@RestController
public class LockController {


    @Resource
    private LockTestService lockService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    RedisLock redisLock;


    @GetMapping("lock")
    public String send(String key) {

        String result = "";
        Long timeOut = 20000L;
        long expireTime = 6L;
        String value =  UUID.randomUUID().toString();
        boolean lock =false;
        try {
            lock= redisLock.tryLock(key,value,timeOut,expireTime);
            if (lock) {
                result=   lockService.test(key, value);
            }else{
                result=  "超时未获得锁";
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            redisLock.unlock(key,value,lock);
        }
        return result;
    }

}
