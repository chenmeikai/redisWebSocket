package com.kenhome.service.impl;

import com.kenhome.service.SecKillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    StringRedisTemplate redisTemplate;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int secKill(String watchKey) throws  Exception {

        redisTemplate.watch(watchKey);

        String lastNumString = redisTemplate.opsForValue().get(watchKey);

        int lastNum = Integer.valueOf(lastNumString);

        logger.info("当前剩余数量为{}", lastNum);

        if (lastNum <= 0) {
            logger.info("已经秒杀完");
            return 0;
        }
        redisTemplate.multi();

        redisTemplate.opsForValue().increment(watchKey, -1);
        List<Object> list = redisTemplate.exec();
        if (list != null) {
            logger.info("抢购成功，当前剩余数量为{}", lastNum);
            return 1;
        } else {
            logger.info("秒杀失败");
            return 2;
        }
    }
}
