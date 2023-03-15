package com.yongoe.webssh.service;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.yongoe.webssh.pojo.ConnectInfo;
import com.yongoe.webssh.pojo.SSHInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
public class WebService {
    //存放ssh连接信息的map
    private static Map<String, ConnectInfo> sshMap = new ConcurrentHashMap<>();
    //线程池
    private ExecutorService executorService = Executors.newCachedThreadPool();
    @Autowired
    private SSHInfo sshInfo;

    /**
     * 初始化连接
     */
    public void initConnection(WebSocketSession session) {
        JSch jSch = new JSch();
        ConnectInfo info = new ConnectInfo();
        info.setJSch(jSch);
        info.setWebSocketSession(session);
        String uuid = String.valueOf(session.getAttributes().get("uuid"));
        //将这个ssh连接信息放入map中
        sshMap.put(uuid, info);
        executorService.execute(() -> connectToSSH(info, session));
    }

    /**
     * 处理客户端发送的数据
     */
    public void recvHandle(String msg, WebSocketSession session) {
        Map<String, Object> attributes = session.getAttributes();
        String userId = String.valueOf(attributes.get("uuid"));
        ConnectInfo info = sshMap.get(userId);
        try {
            transToSSH(info.getChannel(), msg);
        } catch (IOException e) {
            log.error("webssh连接异常");
            e.printStackTrace();
            close(session);
        }
    }

    public void sendMessage(WebSocketSession session, byte[] buffer) throws IOException {
        session.sendMessage(new TextMessage(buffer));
    }

    public void close(WebSocketSession session) {
        Map<String, Object> attributes = session.getAttributes();
        String userId = String.valueOf(attributes.get("uuid"));
        ConnectInfo info = sshMap.get(userId);
        //断开连接
        if (info.getChannel() != null)
            info.getChannel().disconnect();
        //map中移除
        sshMap.remove(userId);
    }

    /**
     * 将消息转发到终端
     */
    private void transToSSH(Channel channel, String command) throws IOException {
        if (channel != null) {
            OutputStream outputStream = channel.getOutputStream();
            outputStream.write(command.getBytes());
            outputStream.flush();
        }
    }

    /**
     * 使用jsch连接终端
     */
    private void connectToSSH(ConnectInfo info, WebSocketSession webSocketSession) {
        Session session = null;
        Channel channel = null;
        InputStream inputStream = null;
        try {
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            //获取jsch的会话
            session = info.getJSch().getSession(sshInfo.getUsername(), sshInfo.getHost(), sshInfo.getPort());
            session.setConfig(config);
            //设置密码
            session.setPassword(sshInfo.getPassword());
            //通道连接 超时时间3s
            //连接  超时时间30s
            session.connect(30000);
            //开启shell通道
            channel = session.openChannel("shell");
            channel.connect(3000);
            //设置channel
            info.setChannel(channel);
            //转发消息
//            transToSSH(channel, "\r");
            inputStream = channel.getInputStream();
            //循环读取
            byte[] buffer = new byte[4096];
            int i = 0;
            //如果没有数据来，线程会一直阻塞在这个地方等待数据。
            while ((i = inputStream.read(buffer)) != -1) {
                sendMessage(webSocketSession, Arrays.copyOfRange(buffer, 0, i));
            }
        } catch (IOException | JSchException e) {
            log.error("连接ssh失败");
            //断开连接后关闭会话
            assert session != null;
            session.disconnect();
            assert channel != null;
            channel.disconnect();
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ex) {
                    log.error("inputStream close失败");
                }
            }
        }
    }
}
