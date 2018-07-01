package com.kenhome.service.impl;

import com.kenhome.service.LockService;
import org.springframework.stereotype.Service;

/**
 * @Author: cmk
 * @Description:
 * @Date: 2018\7\1 0001 18:16
 */
@Service
public class LockServiceImpl implements LockService {

    @Override
    public String test(String key, String value) {

        System.out.println(key + "我获得了锁了哈哈！");
        System.out.println("我先小睡一会，你们请稍等");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("我已经睡醒了");
        String result = key + "我获得了锁了哈哈！";
        return result;
    }
}
