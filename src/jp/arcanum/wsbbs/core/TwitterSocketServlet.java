package jp.arcanum.wsbbs.core;

import javax.servlet.http.HttpServletRequest;

import jp.arcanum.wsbbs.thread.WebSocketManager;

import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;

public class TwitterSocketServlet extends WebSocketServlet{

	/**
	 * TODO
	 * これはとりあえずのしょち
	 */
	public static WebSocketManager _wsmanager = new WebSocketManager("", "");


	@Override
	protected StreamInbound createWebSocketInbound(String subprotocol, HttpServletRequest request) {
		TwitterMessageInbound tmi = new TwitterMessageInbound(subprotocol, request);
		//_wsmanager.addInbound(tmi);
		return tmi;
	}


}
