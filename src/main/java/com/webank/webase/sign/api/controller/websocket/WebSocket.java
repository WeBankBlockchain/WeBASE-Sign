package com.webank.webase.sign.api.controller.websocket;

import com.alibaba.fastjson.JSON;
import com.webank.webase.sign.api.service.SignService;
import com.webank.webase.sign.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
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

    @Autowired
    SignService signService;
    /**
     * onOpen是当用户发起连接时，会生成一个用户的Session 注意此Session是 javax.websocket.Session;
     * 然后我们用userId作为Key Session作为Vaule存入Map中暂存起来
     *
     * @param frontId
     * @param session
     */
    @OnOpen
    public void onOpen(@PathParam("frontId") String frontId, Session session) {
        log.info("====>WebSocketService onOpen: " + frontId);
        if (sessionMap == null) {
            sessionMap = new ConcurrentHashMap<String, Session>();
        }
        sessionMap.put(frontId, session);
    }

    /**
     * onClose 是用户关闭聊天窗时，将用户session移除
     *
     * @param frontId
     */
    @OnClose
    public void onClose(@PathParam("frontId") String frontId) {
        log.info("====>WebSocketService OnClose: " + frontId);
        sessionMap.remove(frontId);
    }

    /**
     * onMessage 实现聊天功能， message是前端传来的JSON字符串。其中含有MessageVo里的信息。根据vo实现点对点/广播聊天。
     *
     * @param message
     */
    @OnMessage
    public void onMessage(String message) throws BaseException {
        log.info("====>WebSocketService OnMessage: " + message);
        MessageVo messageVo = JSON.parseObject(message, MessageVo.class);

            one2one(messageVo);
        }


    /**
     * 当出现异常时候自动调用该方法。
     *
     * @param t
     */
    @OnError
    public void error(Throwable t) {
        t.printStackTrace();
    }

    /**
     * 点对点
     *  session.getAsyncRemote().sendText(message); 即向目标session发送消息。
     *
     */
    private  void one2one(MessageVo vo) throws BaseException {
        Session consumerSession = sessionMap.get(vo.getFrontId());
        if (consumerSession == null) {
            log.info("消息消费者不存在");
        } else {
            SignService signService = applicationContext.getBean(SignService.class);
            String signString = signService.sign(vo);
            vo.setSignedStr(signString);
            consumerSession.getAsyncRemote().sendText(JSON.toJSONString(vo));
        }
    }


}
