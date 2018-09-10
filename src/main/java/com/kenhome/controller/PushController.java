/**
 * Copyright © 2018
 *
 * @Package: RedisController.java
 * @author: Administrator
 * @date: 2018年6月9日 下午5:26:35
 */
package com.kenhome.controller;

import com.kenhome.config.redis.RedisClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 双向链
 * @author: cmk
 * @date: 2018年8月2日 下午23:26:35
 */

@RestController
public class PushController {

    @Autowired
    RedisClient redisClient;

    @GetMapping("push")
    public String send(String channel, String message) {
        for (int i = 0; i < 1000; i++) {
            redisClient.leftPush("demo", i + "");
        }
        return "success";

    }

}
