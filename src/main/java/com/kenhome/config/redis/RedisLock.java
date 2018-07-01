package com.kenhome.config.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Redis 2.6.12 新特性
 * SET key value [EX seconds] [PX milliseconds] [NX|XX] ：
 * EX seconds – 设置键 key 的过期时间，单位时秒
 * PX milliseconds – 设置键 key 的过期时间，单位时毫秒
 * NX – 只有键 key 不存在的时候才会设置 key 的值
 * XX – 只有键 key 存在的时候才会设置 key 的值
 * 如果服务器返回 OK ，那么这个客户端获得锁。
 * 如果服务器返回 NIL ，那么客户端获取锁失败，可以在稍后再重试。
 *
 * @author cmk
 * @version 1.0
 * @date 2018年7月1日
 */
@Component
public class RedisLock {

    private static Logger logger = LoggerFactory.getLogger(RedisLock.class);

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 将key 的值设为value ，当且仅当key 不存在
     */
    public static final String NX = "NX";

    /**
     * seconds — 以秒为单位设置 key 的过期时间
     */
    public static final String EX = "EX";

    /**
     * set后成功的返回值
     */
    public static final String OK = "OK";


    /**
     * 解锁的lua脚本
     */
    public static final String UNLOCK_LUA = "if redis.call(\"get\",KEYS[1]) == ARGV[1] then    return redis.call(\"del\",KEYS[1]) else  return 0 end ";


    final Random random = new Random();


    /**
     * @Description: 加锁
     * @param: lockKey:key
     * @param: lockValue：key值
     * @param: timeOut(ms) 等待超时时间
     * @param: expireTime(s) 锁过期时间
     * @return: boolean :是否成功获得锁
     */
    public boolean tryLock(String lockKey, String lockValue, Long timeOut, Long expireTime) {

        boolean locked = false;
        String keyValue = lockKey + "_" + lockValue;

        // 请求锁超时时间，纳秒
        long timeout = timeOut * 1000000;
        // 系统当前时间，纳秒
        long nowTime = System.nanoTime();
        while ((System.nanoTime() - nowTime) < timeout) {
            if (OK.equalsIgnoreCase(this.set(lockKey, lockValue, expireTime))) {
                locked = true;
                // 上锁成功结束请求
                logger.info(keyValue + "上锁成功");
                return true;
            }
            // 每次请求等待一段时间
            sleep(10, 50000);
        }
        logger.info(keyValue + "等待锁已经超时");
        return locked;
    }

    /**
     * @Description:
     * @param: [lockKey, lockValue, expireTime(过期时间s)]
     * @return: boolean
     */
    public boolean lock(String lockKey, String lockValue, int expireTime) {
        //不存在则添加 且设置过期时间（单位ms）
        String result = set(lockKey, lockValue, expireTime);
        return OK.equalsIgnoreCase(result);
    }

    /**
     * 以阻塞方式的获取锁
     *
     * @return 是否成功获得锁
     */
    public boolean lockBlock(String lockKey, String lockValue, int timeOut, int expireTime) {
        while (true) {
            //不存在则添加 且设置过期时间（单位ms）
            String result = set(lockKey, lockValue, expireTime);
            if (OK.equalsIgnoreCase(result)) {
                return true;
            }
            // 每次请求等待一段时间
            sleep(10, 50000);
        }
    }


    /**
     * 解锁
     * 可以通过以下修改，让这个锁实现更健壮：
     * 不使用固定的字符串作为键的值，而是设置一个不可猜测（non-guessable）的长随机字符串，作为口令串（token）。
     * 不使用 DEL 命令来释放锁，而是发送一个 Lua 脚本，这个脚本只在客户端传入的值和键的口令串相匹配时，才对键进行删除。
     * 这两个改动可以防止持有过期锁的客户端误删现有锁的情况出现。
     */
    public Boolean unlock(String lockKey, String lockValue, boolean lock) {

        // 只有加锁成功并且锁还有效才去释放锁
        if (lock) {

            String keyValue = lockKey + "_" + lockValue;

            logger.info(keyValue + "进行解锁");

            return redisTemplate.execute(new RedisCallback<Boolean>() {
                @Override
                public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                    Object nativeConnection = connection.getNativeConnection();
                    Long result = 0L;

                    List<String> keys = new ArrayList<>();
                    keys.add(lockKey);
                    List<String> values = new ArrayList<>();
                    values.add(lockValue);

                    // 集群模式
                    if (nativeConnection instanceof JedisCluster) {
                        result = (Long) ((JedisCluster) nativeConnection).eval(UNLOCK_LUA, keys, values);
                    }

                    // 单机模式
                    if (nativeConnection instanceof Jedis) {
                        result = (Long) ((Jedis) nativeConnection).eval(UNLOCK_LUA, keys, values);
                    }

                    if (result == 0) {
                        logger.info("Redis分布式锁，解锁{}失败！解锁时间：{}", keyValue, System.currentTimeMillis());
                        return false;
                    }
                    if (result == 1) {
                        logger.info("Redis分布式锁，解锁{}成功！解锁时间：{}", keyValue, System.currentTimeMillis());
                        return true;
                    }
                    return false;
                }
            });
        }
        return false;
    }

    /**
     * 重写redisTemplate的set方法
     * 命令 SET resource-name anystring NX EX max-lock-time 是一种在 Redis 中实现锁的简单方法。
     * 客户端执行以上的命令：
     * 如果服务器返回 OK ，那么这个客户端获得锁。
     * 如果服务器返回 NIL ，那么客户端获取锁失败，可以在稍后再重试。
     *
     * @param key     锁的Key
     * @param value   锁里面的值
     * @param seconds 过去时间（秒）
     * @return
     */
    private String set(final String key, final String value, final long seconds) {
        Assert.isTrue(!StringUtils.isEmpty(key), "key不能为空");
        return redisTemplate.execute(new RedisCallback<String>() {
            @Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                Object nativeConnection = connection.getNativeConnection();
                String result = null;
                // 集群模式
                if (nativeConnection instanceof JedisCluster) {
                    result = ((JedisCluster) nativeConnection).set(key, value, NX, EX, seconds);
                }
                // 单机模式
                if (nativeConnection instanceof Jedis) {
                    result = ((Jedis) nativeConnection).set(key, value, NX, EX, seconds);
                }

                if (!StringUtils.isEmpty(key) && !StringUtils.isEmpty(result)) {
                    logger.info("获取锁{}的时间：{}", key, System.currentTimeMillis());
                }

                return result;
            }
        });
    }

    /**
     * @param millis 毫秒
     * @param nanos  纳秒
     * @Title: seleep
     * @Description: 线程等待时间
     * @author yuhao.wang
     */
    private void sleep(long millis, int nanos) {
        try {
            Thread.sleep(millis, random.nextInt(nanos));
        } catch (InterruptedException e) {
            logger.info("获取分布式锁休眠被中断：", e);
        }
    }


    /**
     * @Description: 获取redis服务器时间
     * @param:
     * @return: long
     */
    public long currentTimeForRedis() {
        return redisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection redisConnection) throws DataAccessException {
                return redisConnection.time();
            }
        });
    }

}


