package com.webank.webase.sign.api.controller.websocket;

import com.alibaba.fastjson.JSON;
import com.webank.webase.sign.api.service.SignService;
import com.webank.webase.sign.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.websocket.WsRemoteEndpointAsync;
import org.fisco.bcos.web3j.protocol.core.Request;
import org.fisco.bcos.web3j.protocol.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@ServerEndpoint("/websocket/{frontId}")
@Component
@Slf4j
public class WebSocket {

    public static Map<String, Session> sessionMap = new ConcurrentHashMap<String, Session>();

    private static ApplicationContext applicationContext;

    //你要注入的service或者dao
    public static void setApplicationContext(ApplicationContext applicationContext) {
        WebSocket.applicationContext = applicationContext;
    }

    private SignService signService = null;

    /**
     * onOpen是当用户发起连接时，会生成一个用户的Session 注意此Session是 javax.websocket.Session;
     *
     * @param frontId
     * @param session
     */
    @OnOpen
    public void onOpen(@PathParam("frontId") String frontId, Session session) {
        log.info("====>WebSocketService onOpen: " + frontId);
        log.info("open sessionId: {}" + session.getId());
        if (sessionMap == null) {
            sessionMap = new ConcurrentHashMap<String, Session>();
        }
        sessionMap.put(frontId, session);
    }

    /**
     *
     * @param frontId
     */
    @OnClose
    public void onClose(@PathParam("frontId") String frontId) {
        log.info("====>WebSocketService OnClose: " + frontId);
        sessionMap.remove(frontId);
    }

    /**
     *  message是前端传来的JSON字符串。其中含有Message里的信息。
     *
     * @param message
     */
    @OnMessage
    public void onMessage(String message) throws BaseException {
        log.info("====>WebSocketService OnMessage: " + message);
        Request messageVo = JSON.parseObject(message, Request.class);

            one2one(messageVo);
        }


    /**
     * 当出现异常时候自动调用该方法。
     *
     * @param t
     */
    @OnError
    public void error(Throwable t) {
        log.info("====>WebSocketService error: " + t.getMessage());
        t.printStackTrace();
    }

    /**
     * session.getAsyncRemote().sendText(message); 即向目标session发送消息。
     */
    private  void one2one(Request vo) throws BaseException {
        Session consumerSession = sessionMap.get(vo.getMethod().split("&")[1]);
        if (consumerSession == null) {
            log.info("消息消费者不存在");
        } else {
            if (signService == null) {
                signService = applicationContext.getBean(SignService.class);
            }
            String signString = signService.signForWebSocket(vo);

            Response response = new Response();
            response.setId(vo.getId());
            response.setJsonrpc(vo.getJsonrpc());
            response.setRawResponse(signString);

            consumerSession.getAsyncRemote().sendText(JSON.toJSONString(response));
        }
    }
}



