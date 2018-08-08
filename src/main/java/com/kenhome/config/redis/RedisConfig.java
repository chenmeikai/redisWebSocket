package com.kenhome.config.redis;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.concurrent.CountDownLatch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * 一个生产者对多个消费者:多个消费者同时消费一个消息，直至所有消费者消费完这个消息，队列才会向所有消费者推送新的消息
 * @author cmk
 * @version 1.0
 * @date 2018年8月4日
 */
@Configuration
public class RedisConfig {


	@Autowired
	private Receiver3 receiver3;

	@Bean
	Receiver receiver(CountDownLatch latch) {
		Receiver receiver =new Receiver();
		receiver.setLatch(latch());
		return receiver ;
	}
	//计数1次
	@Bean
	CountDownLatch latch() {
		return new CountDownLatch(1);
	}

	@Bean
	Receiver2 receiver2(CountDownLatch latch2) {
		Receiver2 receiver2 =new Receiver2();
		receiver2.setLatch(latch2());
		return receiver2 ;
	}
	@Bean
	CountDownLatch latch2() {
		return new CountDownLatch(1);
	}

	/**
	 * 重写Redis序列化方式，使用Json方式:
	 * 当我们的数据存储到Redis的时候，我们的键（key）和值（value）都是通过Spring提供的Serializer序列化到数据库的。RedisTemplate默认使用的是JdkSerializationRedisSerializer，StringRedisTemplate默认使用的是StringRedisSerializer。
	 * Spring Data JPA为我们提供了下面的Serializer：
	 * GenericToStringSerializer、Jackson2JsonRedisSerializer、JacksonJsonRedisSerializer、JdkSerializationRedisSerializer、OxmSerializer、StringRedisSerializer。
	 * 在此我们将自己配置RedisTemplate并定义Serializer。
	 * 
	 * @param redisConnectionFactory
	 * @return
	 */
	@Bean("redisTemplate")
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(redisConnectionFactory);

		Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<Object>(
				Object.class);
		ObjectMapper om = new ObjectMapper();
		om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
		jackson2JsonRedisSerializer.setObjectMapper(om);

		// 设置值（value）的序列化采用Jackson2JsonRedisSerializer。
		redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
		// 设置键（key）的序列化采用StringRedisSerializer。
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setEnableTransactionSupport(true);
		redisTemplate.afterPropertiesSet();
		return redisTemplate;
	}



	// 添加主题为one的监听
	@Bean
	RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
			MessageListenerAdapter listenerAdapter) {
		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.addMessageListener(listenerAdapter, new PatternTopic("one"));
		return container;
	}
	// 监听适配器，并指定监听类执行的方法
	@Bean
	MessageListenerAdapter listenerAdapter(Receiver receiver) {
		return new MessageListenerAdapter(receiver, "receiveMessage");
	}
	// 添加主题为one的监听
	@Bean
	RedisMessageListenerContainer container2(RedisConnectionFactory connectionFactory,
											MessageListenerAdapter listenerAdapter2) {
		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.addMessageListener(listenerAdapter2, new PatternTopic("one"));
		return container;
	}
	// 监听适配器，并指定监听类执行的方法
	@Bean
	MessageListenerAdapter listenerAdapter2(Receiver2 receiver2) {
		return new MessageListenerAdapter(receiver2, "receiveMessage2");
	}


	//另外方式
	@Bean
	RedisMessageListenerContainer container3(RedisConnectionFactory connectionFactory) {
		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.addMessageListener(new MessageListener() {
			@Override
			public void onMessage(Message message, byte[] bytes) {
				String text =message+"";
				System.out.println("container获得消息");
				receiver3.receiveMessage3(text);
			}
		}, new PatternTopic("container"));
		container.addMessageListener(new MessageListener() {
			@Override
			public void onMessage(Message message, byte[] bytes) {
				String text =message+"";
				System.out.println("container2获得消息");
				receiver3.receiveMessage3(text);
			}
		}, new PatternTopic("container2"));


		return container;
	}



}
