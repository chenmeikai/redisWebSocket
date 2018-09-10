package com.kenhome.config.webSocket;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;
import javax.websocket.server.ServerEndpointConfig.Configurator;

/**
 * @Author: cmk
 * @Description: 获取HttpSession
 * @Date: 2018\7\22 0022 20:47
 */
@Component
public class GetHttpSessionConfig extends Configurator {

    Logger log = LoggerFactory.getLogger(GetHttpSessionConfig.class);

    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
        //session有可能为空
        HttpSession session = (HttpSession) request.getHttpSession();
        if (session != null) {
            sec.getUserProperties().put(HttpSession.class.getName(), session);
        } else {
            log.error("modifyHandshake 获取到null session");
        }
    }

}
