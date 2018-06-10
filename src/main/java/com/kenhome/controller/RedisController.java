/**   
 * Copyright © 2018 
 * @Package: RedisController.java 
 * @author: Administrator   
 * @date: 2018年6月9日 下午5:26:35 
 */
package com.kenhome.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kenhome.config.redis.RedisClient;

/**      
 * @Description:TODO  
 * @author: cmk 
 * @date:   2018年6月9日 下午5:26:35     
 */

@RestController
public class RedisController {
	
	@Autowired
	RedisClient redisClient ;
	
	@GetMapping("send")
	public Map<String,Object>  send(String channel,String message){
		
		redisClient.sendMessage(channel, message);
		
		Map<String,Object> result =new HashMap<String, Object>();
		
		result.put("code", 200);
		result.put("desc", "success");
		
		return result;
		
	}

}
