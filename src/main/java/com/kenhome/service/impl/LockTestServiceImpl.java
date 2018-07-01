package com.kenhome.service.impl;

import com.kenhome.service.LockTestService;
import org.springframework.stereotype.Service;

/**
 * @Author: cmk
 * @Description:
 * @Date: 2018\7\1 0001 18:16
 */
@Service
public class LockTestServiceImpl implements LockTestService {

    @Override
    public String test(String key, String value) {

        System.out.println(value+"睡眠中");
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(value+"结束睡眠");
        String result = value + "获得了锁！";
        return result;
    }
}
