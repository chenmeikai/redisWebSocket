package com.kenhome.config.redis;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import com.kenhome.config.SpringContextHolder;
import org.springframework.transaction.annotation.Transactional;


@Component("redisClient")
public class RedisClient {

    private static final Logger logger = LoggerFactory.getLogger(RedisClient.class);

    @Autowired
    @Qualifier("redisTemplate")
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 存储Object
     *
     * @param key   key
     * @param value 值
     * @param num   过期时间
     * @param unit  过期时间单位  如： TimeUnit.MINUTES
     * @throws Exception
     */
    public void setObject(String key, Object value, Integer num, TimeUnit unit) throws Exception {
        redisTemplate.opsForValue().set(key.toString(), value, num.longValue(), unit);
    }

    /**
     * 存储Object
     *
     * @param key   key
     * @param value 值
     * @param num   过期时间，单位分钟
     * @throws Exception
     */
    public void setObject(String key, Object value, int num) throws Exception {
        redisTemplate.opsForValue().set(key.toString(), value, num, TimeUnit.MINUTES);
    }

    /**
     * 存储Object
     *
     * @param key   key
     * @param value 值
     * @throws Exception
     */
    public void setObject(String key, Object value) throws Exception {
        redisTemplate.opsForValue().set(key.toString(), value);
    }

    /**
     * 重设新值，并返回原来的值
     *
     * @param key
     * @param value
     * @return
     * @throws Exception
     */
    public Object getAndSet(String key, Object value) throws Exception {
        return redisTemplate.opsForValue().getAndSet(key, value);
    }

    /**
     * 获取Object
     *
     * @param key key
     * @return String
     * @throws Exception e
     */
    public Object getObject(String key) throws Exception {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 拼接字符串到值的末尾
     *
     * @param key
     * @param str
     * @return
     * @throws Exception
     */
    public int append(String key, String str) throws Exception {
        return redisTemplate.opsForValue().append(key, str);
    }

    /**
     * 截取值
     *
     * @param key
     * @param start
     * @param end
     * @return
     * @throws Exception
     */
    public String getSub(String key, int start, int end) throws Exception {
        return redisTemplate.opsForValue().get(key, start, end);
    }

    /**
     * 获取值的长度
     *
     * @param key
     * @return
     * @throws Exception
     */
    public Long getLength(String key) throws Exception {
        return redisTemplate.opsForValue().size(key);
    }

    /**
     * 增量,并返回增加后的值
     *
     * @param key
     * @param num
     * @return
     * @throws Exception
     */
    public Long increment(String key, Long num) throws Exception {
        return redisTemplate.opsForValue().increment(key, num);
    }

    /**
     * 增量，并返回增加后的值
     *
     * @param key
     * @param num
     * @return
     * @throws Exception
     */
    public double increment(String key, double num) throws Exception {
        return redisTemplate.opsForValue().increment(key, num);
    }


    /**
     * 如果键不存在则新增并返回true,存在则不改变已经有的值并返回false。
     *
     * @param key
     * @param object
     * @return
     * @throws Exception
     */
    public boolean setIfAbsent(String key, Object object) throws Exception {
        return redisTemplate.opsForValue().setIfAbsent(key, object);
    }

    /**
     * 设置map集合
     *
     * @param map
     * @throws Exception
     */
    public void multiSet(Map<String, Object> map) throws Exception {
        redisTemplate.opsForValue().multiSet(map);
        ;
    }

    /**
     * 根据集合取出对应的value值
     *
     * @param lists
     * @return
     * @throws Exception
     */
    public List<Object> multiGet(List<String> lists) throws Exception {
        return redisTemplate.opsForValue().multiGet(lists);
    }

    /**
     * 删除key
     *
     * @param key
     * @throws Exception
     */
    public void delete(String key) throws Exception {
        redisTemplate.delete(key);
        ;
    }

    /**
     * 批量删除key
     *
     * @param keys
     * @throws Exception
     */
    public void delete(List<String> keys) throws Exception {
        redisTemplate.delete(keys);
    }

    /**
     * 根据通配符字符串删除key
     *
     * @param pattern
     * @throws Exception
     */
    public void deleteByPattern(String pattern) throws Exception {
        Set<String> keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }

    /**
     * 清空所有key
     *
     * @throws Exception
     */
    public void clear() throws Exception {
        Set<String> keys = redisTemplate.keys("*");
        redisTemplate.delete(keys);
    }

    /**
     * 获取key过期时间
     *
     * @param key
     * @return
     * @throws Exception
     */
    public Long getExpire(String key) throws Exception {
        return redisTemplate.getExpire(key);
    }

    /**
     * 获取key过期时间，带单位
     *
     * @param key
     * @param unit
     * @return
     * @throws Exception
     */
    public Long getExpire(String key, TimeUnit unit) throws Exception {
        return redisTemplate.getExpire(key, unit);
    }

    /**
     * 是否存在该key
     *
     * @param key
     * @return
     * @throws Exception
     */
    public boolean hasKey(String key) throws Exception {
        return redisTemplate.hasKey(key);
    }


    /**
     * 设置过期时间
     *
     * @param key
     * @param num
     * @param unit
     * @return
     * @throws Exception
     */
    public boolean expire(String key, Long num, TimeUnit unit) throws Exception {
        return redisTemplate.expire(key, num, unit);
    }


    /**
     * 发送队列消息
     *
     * @param channel
     * @param message
     */
    public void sendMessage(String channel, String message) {
        redisTemplate.convertAndSend(channel, message);
    }


    /**
     * @Description:  双向链：左压栈
     * @param: [key, message]
     * @return: 长度
     */
    public Long leftPush(String key, String message) {
       return  redisTemplate.opsForList().leftPush(key,message);
    }
    /**
     * @Description:  双向链：右压栈
     * @param: [key, message]
     * @return: 长度
     */
    public Long rightPush(String key, String message) {
       return  redisTemplate.opsForList().rightPush(key,message);
    }
    /**
     * @Description: 双向链：左出栈
     * @param: [key]
     * @return: java.lang.String
     */
    public String leftPop(String key) {
        return (String) redisTemplate.opsForList().leftPop(key);
    }
    /**
     * @Description: 双向链：右出栈
     * @param: [key]
     * @return: java.lang.String
     */
    public String rightPop(String key) {
        return (String) redisTemplate.opsForList().rightPop(key);
    }
    /**
     * @Description: 双向链：阻塞左出栈
     * @param: [key]
     * @return: java.lang.String
     */
    public String leftPopBlock(String key,Long num,TimeUnit timeUnit) {
        return (String) redisTemplate.opsForList().leftPop(key,num,timeUnit);
    }
    /**
     * @Description: 双向链：阻塞右出栈
     * @param: [key]
     * @return: java.lang.String
     */
    public String rightPopBlock(String key,Long num,TimeUnit timeUnit) {
        return (String) redisTemplate.opsForList().rightPop(key,num,timeUnit);
    }
    /**
     * @Description: 双向链：获取长度
     * @param: [key]
     * @return: java.lang.String
     */
    public Long getListSize(String key) {
        return  redisTemplate.opsForList().size(key);
    }
    
    /**
     * @Description: 双向链：获取list集合
     * @param: [key, start, end] start:起始位置为0 end:传-1获取全部
     * @return: java.util.List<Object>
     */
    public List<Object> getList(String key,Long start,Long end) {
        return  redisTemplate.opsForList().range(key,start,end);
    }
    /**
     * @Description: 双向链：移除object
     * @param: [key, index, object] index:起始位置，传0移除所有object
     * @return: java.lang.Long
     */
    public  Long  removeList(String key,Long index,Object object) {
        return  redisTemplate.opsForList().remove(key,index,object);
    }






    /**
     * @Description: 原子性操作秒杀 返回0：秒杀结束；1：抢购成功；2：抢购失败(缺点：会阻塞，返回速度慢)
     * @auther: cmk
     * @date: 2018-7-4 13:43
     * @param: [watchKey]
     * @return: int
     */
    public int secKill2(String watchKey, int num) {

        int lastNum = (int) redisTemplate.opsForValue().get(watchKey);

        logger.info("当前剩余数量为{}", lastNum);

        if (lastNum <= 0) {
            logger.info("已经秒杀完");
            return 0;
        }
        /*原子性*/
        long ret = redisTemplate.opsForValue().increment(watchKey, num);

        logger.info("ret:" + ret);
        if (ret >= 0) {
            logger.info("抢购成功，当前剩余数量为{}", ret);
            return 1;
        } else {
            logger.info("秒杀失败");
            return 2;
        }
    }


    /**
     *
     * @Description: 乐观锁操作秒杀 返回0：秒杀结束；1：抢购成功；2：抢购失败；（缺点：会返回失败，可能导致抢购人数大于商品数，最终抢购的商品数却还有剩）
     * @auther: cmk
     * @date: 18:00
     * @param: [watchKey, num]
     * @return: int
     *
     */
    public int secKill(String watchKey, int num) {
        try {
            redisTemplate.execute(new SessionCallback() {
                @Override
                public Object execute(RedisOperations operations) throws DataAccessException {

                    operations.watch(watchKey);

                    int origin = (int) operations.opsForValue().get(watchKey);

                    operations.multi();

                    logger.info("当前剩余数量为{}", origin);

                    if (origin <= 0) {
                        logger.info("已经秒杀完");
                        return 0;
                    }
                    operations.opsForValue().increment(watchKey,num);
                    List<Object> result = operations.exec();

                    System.out.println("set:" + origin + num + " rs:" + result);

                    if (result.size()>0) {
                        logger.info("抢购成功，当前剩余数量为{}", origin - 1);
                        return 1;
                    } else {
                        logger.info("秒杀失败");
                        return 2;
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 2;
    }

}