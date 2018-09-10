package com.kenhome;

import com.kenhome.config.redis.RedisClient;
import com.kenhome.run.RunApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

/**
 * @Author: cmk
 * @Description:
 * @Date: 2018\8\4 0004 19:41
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = RunApplication.class)
public class RedisTest {

    @Autowired
    private RedisClient redisClient;

    @Test
    public void test() {
        //删除该链
        try {
            redisClient.delete("list");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //从右入栈
        for (int i = 0; i < 50; i++) {
            String text = "number:" + i;
            System.out.println("list长度" + redisClient.rightPush("list", text));
        }

        System.out.println("入栈结束，查询链中内容");

        List<Object> list = redisClient.getList("list", 0L, -1L);
        //从左开始遍历（对应从右入栈，先入栈先遍历出）
        for (Object object : list) {
            String text = (String) object;
            System.out.println("双向链内容:" + text);
        }


    }
}
