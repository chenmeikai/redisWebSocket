package com.kenhome.config.redis;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import com.kenhome.config.SpringContextHolder;
import org.springframework.transaction.annotation.Transactional;


@Component("redisClient")
@Lazy//懒加载
public class RedisClient {

    private static final Logger logger = LoggerFactory.getLogger(RedisClient.class);

    private RedisTemplate<String, Object> redisTemplate = SpringContextHolder.getBean("redisTemplate");

    /**
     * 存储Object
     * @param key   key
     * @param value 值
     * @param num  过期时间
     * @param unit  过期时间单位  如： TimeUnit.MINUTES
     * @throws Exception
     */
    public void setObject(String key, Object value,Integer num,TimeUnit unit) throws Exception {
    	redisTemplate.opsForValue().set(key.toString(), value, num.longValue(), unit);
    }

    /**
     * 存储Object
     * @param key  key
     * @param value  值
     * @param num 过期时间，单位分钟
     * @throws Exception
     */
    public void setObject(String key, Object value,int num) throws Exception {
    	redisTemplate.opsForValue().set(key.toString(), value, num, TimeUnit.MINUTES);
    }

    /**
     * 存储Object
     * @param key  key
     * @param value  值
     * @throws Exception
     */
    public void setObject(String key, Object value) throws Exception {
    	redisTemplate.opsForValue().set(key.toString(), value);
    }

    /**
     * 重设新值，并返回原来的值
     * @param key
     * @param value
     * @return
     * @throws Exception
     */
    public Object getAndSet(String key,Object value) throws Exception{
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
    	return   redisTemplate.opsForValue().get(key);
    }

    /**
     * 拼接字符串到值的末尾
     * @param key
     * @param str
     * @return
     * @throws Exception
     */
    public int append(String key,String str) throws Exception{
    	return redisTemplate.opsForValue().append(key, str);
    }

    /**
     * 截取值
     * @param key
     * @param start
     * @param end
     * @return
     * @throws Exception
     */
    public String getSub(String key,int start,int end) throws Exception{
    	return redisTemplate.opsForValue().get(key, start,end);
    }

    /**
     * 获取值的长度
     * @param key
     * @return
     * @throws Exception
     */
    public Long getLength(String key) throws Exception{
    	return redisTemplate.opsForValue().size(key);
    }

    /**
     * 增量,并返回增加后的值
     * @param key
     * @param num
     * @return
     * @throws Exception
     */
    public Long increment(String key,Long num) throws Exception{
    	return redisTemplate.opsForValue().increment(key,num);
    }

    /**
     * 增量，并返回增加后的值
     * @param key
     * @param num
     * @return
     * @throws Exception
     */
    public double increment(String key,double num) throws Exception{
    	return redisTemplate.opsForValue().increment(key,num);
    }


    /**
     * 如果键不存在则新增并返回true,存在则不改变已经有的值并返回false。
     * @param key
     * @param object
     * @return
     * @throws Exception
     */
    public boolean setIfAbsent(String key,Object object) throws Exception{
        return redisTemplate.opsForValue().setIfAbsent(key, object);
    }

    /**
     * 设置map集合
     * @param map
     * @throws Exception
     */
    public void multiSet(Map<String,Object> map) throws Exception{
    	 redisTemplate.opsForValue().multiSet(map);;
    }

    /**
     * 根据集合取出对应的value值
     * @param lists
     * @return
     * @throws Exception
     */
    public List<Object> multiGet(List<String> lists) throws Exception{
    	 return redisTemplate.opsForValue().multiGet(lists);
    }

    /**
     * 删除key
     * @param key
     * @throws Exception
     */
    public void delete(String key) throws Exception{
    	 redisTemplate.delete(key);;
    }

    /**
     * 批量删除key
     * @param keys
     * @throws Exception
     */
    public void delete(List<String> keys) throws Exception{
    	redisTemplate.delete(keys);
    }

    /**
     * 根据通配符字符串删除key
     * @param pattern
     * @throws Exception
     */
    public void deleteByPattern(String pattern) throws Exception{
    	Set<String> keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }

    /**
     * 清空所有key
     * @throws Exception
     */
    public void clear() throws Exception{
    	Set<String> keys = redisTemplate.keys("*");
    	redisTemplate.delete(keys);
    }

    /**
     * 获取key过期时间
     * @param key
     * @return
     * @throws Exception
     */
    public Long getExpire(String key) throws Exception{
    	return redisTemplate.getExpire(key);
    }

    /**
     * 获取key过期时间，带单位
     * @param key
     * @param unit
     * @return
     * @throws Exception
     */
    public Long getExpire(String key,TimeUnit unit) throws Exception{
    	 return redisTemplate.getExpire(key,unit);
    }

    /**
     * 是否存在该key
     * @param key
     * @return
     * @throws Exception
     */
    public boolean hasKey(String key) throws Exception{
    	return redisTemplate.hasKey(key);
    }


    /**
     * 设置过期时间
     * @param key
     * @param num
     * @param unit
     * @return
     * @throws Exception
     */
    public boolean expire(String key,Long num ,TimeUnit unit) throws Exception{
    	return redisTemplate.expire(key,num,unit);
    }



    /**
     * 发送消息
     * @param channel
     * @param message
     */
    public void sendMessage(String channel,String message) {
    	redisTemplate.convertAndSend(channel, message);
    }


    public int secKill(String watchKey) {

        ValueOperations<String, Object> valueOperations  = redisTemplate.opsForValue();

        redisTemplate.watch(watchKey);

        int lastNum = (int) valueOperations.get(watchKey);

        logger.info("当前剩余数量为{}",lastNum);

        if(lastNum <=0){
            logger.info("已经秒杀完");
            return 0;
        }

        redisTemplate.multi();

        valueOperations.increment(watchKey, -1);
        List<Object> list = redisTemplate.exec();
        if (list != null) {
            logger.info("抢购成功，当前剩余数量为{}",lastNum);
            redisTemplate.setEnableDefaultSerializer(false);
            return 1;
        } else {
            logger.info("秒杀失败");
            redisTemplate.setEnableDefaultSerializer(false);
            return 2;

        }



    }


}  
