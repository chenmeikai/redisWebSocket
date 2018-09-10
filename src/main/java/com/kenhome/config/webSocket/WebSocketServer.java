/**
 * Copyright © 2018
 *
 * @Package: sadf.java
 * @author: Administrator
 * @date: 2018年6月4日 下午10:21:16
 */
package com.kenhome.config.webSocket;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.kenhome.config.SpringContextHolder;
import com.kenhome.config.redis.RedisClient;


/**
 *
 * @Description:场景：使用webSocket与客户端建立连接后，客户端发送数据，使用redis队列处理数据，将处理结果通过webSocket推送给该用户
 * @author: cmk
 * @date: 2018年6月10日 上午11:29:20
 */
@ServerEndpoint("/websocket/BBTrade/{userId}")
@Component
public class WebSocketServer {

    static Logger log = LoggerFactory.getLogger(WebSocketServer.class);

    @Autowired
    private RedisClient redisClient;

    // 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;

    // concurrent包的线程安全Map，用来存放每个客户端对应的MyWebSocket对象,便于根据key获取对应的客户端
    public static Map<Long, WebSocketServer> webSocketMap = new ConcurrentHashMap<Long, WebSocketServer>();

    // 与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    // 接收userId
    private Long userId = null;


    /**
     * 连接建立成功调用的方法
     * @param session
     * @param userId
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") Long userId) {
        this.session = session;

        // 添加进map
        if (!webSocketMap.containsKey(userId)) {
            webSocketMap.put(userId, this);
        }

        addOnlineCount(); // 在线数加1
        log.info("有新窗口开始监听:" + userId + ",当前在线人数为" + getOnlineCount());
        this.userId = userId;
        try {
            sendMessage(userId + "连接成功");
        } catch (IOException e) {
            log.error("websocket IO异常");
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        webSocketMap.remove(this.userId); // 从Map中删除
        subOnlineCount(); // 在线数减1
        log.info("有一连接关闭！当前在线人数为" + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     * @param message
     * @param session
     *  客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {

        //发送到队列处理
        redisClient.sendMessage("one", message);

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
     * 给客户端推送消息
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    /**
     * 给特定用户发送消息
     *
     * @param userId
     * @param message
     * @throws IOException
     */
    public static void sendInfo(Long userId, String message) throws IOException {

        WebSocketServer server = webSocketMap.get(userId);

        if (server == null) {
            log.warn(userId + "用户已不在连接中，发送消息取消");
            return;
        }
        server.sendMessage(message);
    }

    /**
     * 群发消息
     *
     * @param message
     * @throws IOException
     */
    public static void sendInfo(String message) throws IOException {

        for (WebSocketServer item : webSocketMap.values()) {
            item.sendMessage(message);
        }
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }
}