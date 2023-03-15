package com.yongoe.webssh.pojo;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import lombok.Data;
import org.springframework.web.socket.WebSocketSession;

@Data
public class ConnectInfo {

    private WebSocketSession webSocketSession;
    private JSch jSch;
    private Channel channel;

}
