package com.kenhome.task;

import com.alibaba.fastjson.JSON;
import com.kenhome.config.redis.RedisClient;
import com.kenhome.model.CancelOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @Author: cmk
 * @Description: 定时任务，自动取消订单 思路（支持分布式） : 创建订单时，生成下订单id和自动取消到期时间的记录，将记录有序的放入redis的list链中，通过定时器从链的最先进的记录开始遍历，比较到期时间，如果到期则执行自动取消业务，执行完后继续遍历下个记录直至遍历到记录未达到到期时间，将该记录放回原来的list链的位置，不再执行遍历（因为最旧的记录没达到过期时间，其余的更无法达到过期时间，因为list是按时间有序的入栈的）；注意：当消费者手动取消订单和确认付款时，需要移除list对应的订单。
 * @Date: 2018\8\4 0004 23:21
 */
@Component
public class CancelOrderJob {


    @Autowired
    private RedisClient redisClient;

    @Scheduled(cron = "0 0/1 * * * *")
    public void cancel(){
         Long nowLine =System.currentTimeMillis();
         while (true){

             String orderJobJson = redisClient.rightPop("cancelOrder");
             //没有订单，则跳出循环
             if (StringUtils.isEmpty(orderJobJson)){
                 break;
             }

             try {
                 CancelOrder cancelOrder = JSON.parseObject(orderJobJson,CancelOrder.class);
                 Long overLine =cancelOrder.getOverLine();

                 //如果最旧的订单未到期，则将该订单放回list原来的位置中，并跳出循环
                 if(nowLine < overLine){
                     redisClient.rightPush("cancelOrder",orderJobJson);
                     break;
                 }

                 //TODO 业务处理：自动取消订单 ，注意：订单的手动取消和确认付款需要在订单取消链移除该订单

             } catch (Exception e) {
                 //TODO 将处理异常的cancelOrder 记录下来，不能放回list中，否则会导致无限循环
                 e.printStackTrace();
             }


         }

    }
}
