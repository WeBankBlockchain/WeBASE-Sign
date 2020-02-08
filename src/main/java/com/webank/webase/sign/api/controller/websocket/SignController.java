package com.webank.webase.sign.api.controller.websocket;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/websocket/{userAddress}")
@Component
@Slf4j
public class SignController {
}
