package com.kenhome.task;

import com.alibaba.fastjson.JSON;
import com.kenhome.config.redis.RedisClient;
import com.kenhome.model.CancelOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * @Author: cmk
 * @Description: 项目启动时执行，自动取消订单 思路（支持分布式） : 创建订单时，生成下订单id和自动取消到期时间的记录，将记录有序的放入redis的list链中，通过定时器从链的最先进的记录开始遍历，比较到期时间，如果到期则执行自动取消业务，执行完后继续遍历下个记录直至遍历到记录未达到到期时间，将该记录放回原来的list链的位置，不再执行遍历（因为最旧的记录没达到过期时间，其余的更无法达到过期时间，因为list是按时间有序的入栈的）；注意：当消费者手动取消订单和确认付款时，需要移除list对应的订单。
 * @Date: 2018\8\4 0004 23:21
 */

public class CancelOrderTask implements Runnable {


    private RedisClient redisClient;

    public CancelOrderTask(RedisClient redisClient) {
        this.redisClient = redisClient;
    }

    @Override
    public void run() {

        while (true) {

            String orderJobJson = redisClient.rightPopBlock("cancelOrder", 10L, TimeUnit.MINUTES);


            //没有订单，则重新进入循环
            if (StringUtils.isEmpty(orderJobJson)) {

                //没有，则可休眠一个过期时间再至下个循环，如订单自动取消时间为30min
                try {
                    Long cancelTimeStamp = 1800000L;
                    Thread.sleep(cancelTimeStamp);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }

            try {

                CancelOrder cancelOrder = JSON.parseObject(orderJobJson, CancelOrder.class);

                //当前时间
                Long nowLine = System.currentTimeMillis();
                //到期时间
                Long overLine = cancelOrder.getOverLine();

                //如果最旧的订单未到期，则将该订单放回list原来的位置中，休眠相差时间，再进入下个循环
                if (nowLine < overLine) {
                    redisClient.rightPush("cancelOrder", orderJobJson);

                    Long sleepTime = overLine - nowLine;
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    continue;
                }

                //TODO 业务处理：自动取消订单 ，注意：订单的手动取消和确认付款时list要移除该订单

            } catch (Exception e) {
                //TODO 将处理异常的cancelOrder 记录下来，不能放回list中，否则会导致无限循环
                e.printStackTrace();
            }
        }


    }
}
