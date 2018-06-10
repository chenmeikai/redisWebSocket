/**   
 * Copyright © 2018 
 * @Package: WebSocketConfig.java 
 * @author: Administrator   
 * @date: 2018年6月4日 下午10:19:02 
 */
package com.kenhome.config.webSocket;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**      
 * @Description:开启WebSocket  
 * @author: cmk 
 * @date:   2018年6月4日 下午10:19:02     
 */

@Component
public class WebSocketConfig {
	
	@Bean  
    public ServerEndpointExporter serverEndpointExporter() {  
        return new ServerEndpointExporter();  
    } 

}
