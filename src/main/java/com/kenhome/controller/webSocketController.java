/**   
 * Copyright © 2018 
 * @Package: RedisController.java 
 * @author: Administrator   
 * @date: 2018年6月9日 下午5:26:35 
 */
package com.kenhome.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**      
 * @Description:TODO  
 * @author: cmk 
 * @date:   2018年6月9日 下午5:26:35     
 */

@Controller
public class webSocketController {
	
	
	
	@GetMapping("webSocket")
	public String  webSocket(){
		
		return "websocket/socket";
		
	}
	
	@GetMapping("demo")
	public String  webSocket2(){
		
		return "websocket/demo";
		
	}

}
