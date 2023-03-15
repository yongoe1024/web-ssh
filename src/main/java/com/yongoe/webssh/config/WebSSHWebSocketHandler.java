package com.yongoe.webssh.config;

import com.yongoe.webssh.service.WebService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.util.Map;

@Slf4j
@Component
public class WebSSHWebSocketHandler implements WebSocketHandler {
    @Autowired
    private WebService webService;

    /**
     * 用户连接上WebSocket的回调
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) {
        log.info("用户:{},连接WebSSH", webSocketSession.getAttributes().get("uuid"));
        //调用初始化连接
        webService.initConnection(webSocketSession);
    }

    /**
     * 收到消息的回调
     */
    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) {
        if (webSocketMessage instanceof TextMessage) {
            log.info("用户:{},发送命令:{}", webSocketSession.getAttributes().get("uuid"), webSocketMessage.getPayload());
            //调用service接收消息
            webService.recvHandle(((TextMessage) webSocketMessage).getPayload(), webSocketSession);
        } else if (webSocketMessage instanceof BinaryMessage) {

        } else if (webSocketMessage instanceof PongMessage) {

        } else {
            log.warn("Unexpected WebSocket message type: " + webSocketMessage);
        }
    }

    /**
     * 出现错误的回调
     */
    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) {
        log.error("数据传输错误");
    }

    /**
     * 连接关闭的回调
     */
    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) {
        Map<String, Object> attributes = webSocketSession.getAttributes();
        log.info("用户:{}断开webssh连接", attributes.get("uuid"));
        //调用service关闭连接
        webService.close(webSocketSession);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
