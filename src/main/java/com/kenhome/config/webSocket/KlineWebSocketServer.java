/**   
 * Copyright © 2018 
 * @author: Administrator   
 * @date: 2018年6月4日 下午10:21:16 
 */
package com.kenhome.config.webSocket;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 
 * @Description:场景：使用webSocket与客户端建立连接后，客户端发送k线图请求参数，记录用户的请求参数，推送对应的k线图数据,当用户再次请求时，重新记录用户的请求参数，再推送数据
 * @author: cmk
 * @date: 2018年6月10日 上午11:29:20
 */
@ServerEndpoint("/websocket/Kline/v1")
@Component
public class KlineWebSocketServer {

	static Logger log = LoggerFactory.getLogger(KlineWebSocketServer.class);

	// 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
	private static int onlineCount = 0;

	// concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
	public static CopyOnWriteArraySet<KlineWebSocketServer> webSocketSet = new CopyOnWriteArraySet<KlineWebSocketServer>();

	// concurrent包的线程安全Map，用来存放各类型的kline数据
	public static Map<String, Object> kLineDataMap = new ConcurrentHashMap<String, Object>();

	public static CopyOnWriteArraySet<String> kLineTypeSet = new CopyOnWriteArraySet<String>();


	// 与某个客户端的连接会话，需要通过它来给客户端发送数据
	private Session session;

	// 交易对
	private String symbol;

	// 时间段
	private String type;

	// 限制数量
	private Integer size;

	// 请求数据类型
	private String category;

	/**
	 * 连接建立成功调用的方法
	 * 
	 * @param session
	 * @param userId
	 */
	@OnOpen
	public void onOpen(Session session) {

		this.session = session;
		webSocketSet.add(this); // 加入set中

		addOnlineCount(); // 在线数加1

		log.info("Kline数据新增连接:" + ",当前在线人数为" + getOnlineCount());

	}

	/**
	 * 连接关闭调用的方法
	 */
	@OnClose
	public void onClose() {
		webSocketSet.remove(this); // 从set中删除
		subOnlineCount(); // 在线数减1
		log.info("有一连接关闭！当前在线人数为" + getOnlineCount());
	}

	/**
	 * 收到客户端消息后调用的方法
	 * 
	 * @param message
	 * @param session
	 *            客户端发送过来的消息
	 */
	@OnMessage
	public void onMessage(String message, Session session) {

		log.info("收到K线图请求参数：" + message);
		
		String json ="{\"data\":{\"datas\":[[1528938120000,50.000000000000,50.000000000000,50.000000000000,50.000000000000,10.000000000000]]},\"success\":true}";

		try {
			
			sendMessage(json);
			
		} catch (IOException e) {
			
			e.printStackTrace();
			
		}

		
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
	 * 群发消息
	 * 
	 * @param message
	 * @throws IOException
	 */
	public static void sendInfo(String message) throws IOException {

	}

	public static synchronized int getOnlineCount() {
		return onlineCount;
	}

	public static synchronized void addOnlineCount() {
		KlineWebSocketServer.onlineCount++;
	}

	public static synchronized void subOnlineCount() {
		KlineWebSocketServer.onlineCount--;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

}