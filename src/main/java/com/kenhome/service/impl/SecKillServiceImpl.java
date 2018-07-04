package com.kenhome.service.impl;

import com.kenhome.config.SpringContextHolder;
import com.kenhome.config.redis.RedisClient;
import com.kenhome.service.SecKillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Author: cmk
 * @Description:
 * @Date: 2018\7\1 0001 18:16
 */
@Service
public class SecKillServiceImpl implements SecKillService {

    Logger logger = LoggerFactory.getLogger(SecKillServiceImpl.class);

    @Autowired
    RedisClient redisClient;


    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    @Override
    public int secKill(String watchKey) throws Exception {

       return  redisClient.secKill(watchKey,-1);

    }
}
