/**
 * Copyright © 2018
 *
 * @Package: sadf.java
 * @author: Administrator
 * @date: 2018年6月4日 下午10:21:16
 */
package com.kenhome.config.webSocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;


@ServerEndpoint(value = "/webSocket/session", configurator = GetHttpSessionConfig.class)
@Component
public class SessionServer {

    static Logger log = LoggerFactory.getLogger(SessionServer.class);

    private String name;

    private static int onlineCount = 0;

    private static CopyOnWriteArraySet<SessionServer> webSocketSet = new CopyOnWriteArraySet<SessionServer>();

    private Session session;


    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        this.session = session;
        webSocketSet.add(this);
        //在线数加1
        addOnlineCount();
        log.info("有新窗口开始监听: 当前在线人数为" + getOnlineCount());

        //获取httpSession
        HttpSession httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
        name = (String) httpSession.getAttribute("name");
        log.info("获得session的name属性是:{}", name);

        try {
            sendMessage(name + "连接成功");
        } catch (IOException e) {
            log.error("webSocket IO异常");
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        webSocketSet.remove(this);
        //在线数减1
        subOnlineCount();
        log.info("有一连接关闭！当前在线人数为" + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {

        log.info("收到来自窗口" + this.toString() + "的信息:" + message);


    }

    /**
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误");
        error.printStackTrace();
    }

    /**
     * 实现服务器主动推送
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        SessionServer.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        SessionServer.onlineCount--;
    }

}