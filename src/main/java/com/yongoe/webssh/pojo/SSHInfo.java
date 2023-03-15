package com.yongoe.webssh.pojo;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties( prefix = "ssh")
public class SSHInfo {

    String username;
    String password;
    int port;
    String host;
}
