/**
 * Copyright © 2018
 *
 * @Package: RedisController.java
 * @author: Administrator
 * @date: 2018年6月9日 下午5:26:35
 */
package com.kenhome.controller;

import com.kenhome.config.redis.RedisLock;
import com.kenhome.service.LockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Description:TODO
 * @author: cmk
 * @date: 2018年6月9日 下午5:26:35
 */

@RestController
public class LockController {


    @Resource
    private LockService lockService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;


    @GetMapping("lock")
    public String send2(String key) {

        String result = "";
        Long timeOut = 20000L;
        int expireTime = 5;
        String value = "haha";

        RedisLock redisLock =  new RedisLock(stringRedisTemplate,key,expireTime,timeOut);

        try {

             redisLock =  new RedisLock(stringRedisTemplate,key,expireTime,timeOut);

            if (redisLock.tryLock()) {
                result=   lockService.test(key, value);
            }else{
                result=  "请重试";
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            redisLock.unlock();
        }


        return result;

    }

}
