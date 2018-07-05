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

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;


@ServerEndpoint("/webSocket/heart")
@Component
public class HeartServer {

    static Logger log = LoggerFactory.getLogger(HeartServer.class);

    static final String heart = "alive";
    static final String recall = "fine";

    private static int onlineCount = 0;

    private static CopyOnWriteArraySet<HeartServer> webSocketSet = new CopyOnWriteArraySet<HeartServer>();

    private Session session;


    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        webSocketSet.add(this);
        //在线数加1
        addOnlineCount();
        log.info("有新窗口开始监听: 当前在线人数为" + getOnlineCount());

        try {
            sendMessage("连接成功");
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

        boolean flag  =isCheckHeart(message);
        if(flag==true){
            return ;
        }
        //不是心跳检测则往下执行逻辑...

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

    /**
     * 是否是心跳检测
     */
    boolean isCheckHeart(String message) {
        boolean flag =false;
        if (heart.equals(message)) {
            flag=true;
            try {
                sendMessage(recall);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return flag ;
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        HeartServer.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        HeartServer.onlineCount--;
    }
}