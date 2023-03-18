package cn.ctrlcv.im.serve.utils;

import cn.ctrlcv.im.codec.pack.MessagePack;
import cn.ctrlcv.im.common.enums.command.Command;
import cn.ctrlcv.im.common.model.ClientInfo;
import cn.ctrlcv.im.common.model.UserSession;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Class Name: MessageProducer
 * Class Description: 消息投递工具，向用户发送消息
 *
 * @author liujm
 * @date 2023-03-17
 */
@Slf4j
@Component
public class MessageProducer {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private UserSessionUtils userSessionUtils;


    /**
     * 投递消息给用户
     * <p>对{@link #sendMessage(UserSession, Object)}的封装</p>
     *
     * @param toId 接收用户ID
     * @param command 投递指令
     * @param msg 消息内容
     * @param session {@link UserSession}
     * @return 是否投递成功
     */
    public boolean sendPack(String toId, Command command, Object msg, UserSession session) {

        MessagePack<JSONObject> messagePack = new MessagePack<>();
        messagePack.setCommand(command.getCommand());
        messagePack.setToId(toId);
        messagePack.setCommand(session.getClientType());
        messagePack.setAppId(session.getAppId());
        messagePack.setImei(session.getImei());
        messagePack.setData(JSONObject.parseObject(JSONObject.toJSONString(msg)));

        return this.sendMessage(session, JSONObject.toJSONString(messagePack));

    }

    /**
     * 发送消息
     * <p>最底层的发送，避免直接调用</p>
     *
     * @param userSession {@link UserSession}
     * @param msg         要发送的消息
     * @return 消息是否发送成功
     */
    public boolean sendMessage(UserSession userSession, Object msg) {
        try {
            log.info(" ===== 投递消息：{} ===== ", msg);
            rabbitTemplate.convertAndSend("", String.valueOf(userSession.getBrokerId()),  msg);
            return true;
        } catch (Exception e) {
            log.error(" ===== 投递消息失败：{} ===== ", e.getMessage());
            e.printStackTrace();
        }

        return false;
    }


    /**
     * 发送给用户所有端的方法
     *
     * @param toId 用户ID
     * @param command 消息指令
     * @param data 消息内容
     * @param appId 应用ID
     * @return 发送成功的客户端 {@link List}<{@link ClientInfo}>
     */
    public List<ClientInfo> sendToUser(String toId, Command command, Object data, Integer appId){
        List<UserSession> userSession = userSessionUtils.getUserSession(appId, toId);
        List<ClientInfo> list = new ArrayList<>();
        for (UserSession session : userSession) {
            boolean b = sendPack(toId, command, data, session);
            if(b){
                list.add(new ClientInfo(session.getAppId(),session.getClientType(),session.getImei()));
            }
        }
        return list;
    }


    /**
     * 发送给用户所有端的方法
     *
     * @param toId 用户ID
     * @param command 消息指令
     * @param data 消息内容
     * @param appId 应用ID
     * @return 发送成功的客户端 {@link List}<{@link ClientInfo}>
     */
    public void sendToUser(String toId, Integer clientType,String imei, Command command, Object data, Integer appId){
        if(clientType != null && StringUtils.isNotBlank(imei)){
            ClientInfo clientInfo = new ClientInfo(appId, clientType, imei);
            sendToUserExceptClient(toId,command,data,clientInfo);
        }else{
            sendToUser(toId,command,data,appId);
        }
    }

    /**
     * 发送给某个用户的指定客户端
     *
     * @param toId 用户ID
     * @param command 指令
     * @param data 消息内容
     * @param clientInfo 客户端信息
     */
    public void sendToUser(String toId, Command command, Object data, ClientInfo clientInfo){
        UserSession userSession = userSessionUtils.getUserSession(clientInfo.getAppId(), toId, clientInfo.getClientType(),
                clientInfo.getImei());
        sendPack(toId,command,data,userSession);
    }

    /**
     * 发送给除了某一端的其他端
     *
     * @param toId 用户ID
     * @param command 指令
     * @param data 消息内容
     * @param clientInfo 客户端信息
     */
    public void sendToUserExceptClient(String toId, Command command, Object data, ClientInfo clientInfo){
        List<UserSession> userSession = userSessionUtils.getUserSession(clientInfo.getAppId(), toId);
        for (UserSession session : userSession) {
            if(!isMatch(session,clientInfo)){
                sendPack(toId,command,data,session);
            }
        }
    }


    /**
     * 判断两个客户端是否相同
     *
     * @param sessionDto {@link UserSession}
     * @param clientInfo {@link ClientInfo}
     * @return 是否相同
     */
    private boolean isMatch(UserSession sessionDto, ClientInfo clientInfo) {
        return
                Objects.equals(sessionDto.getAppId(), clientInfo.getAppId())
                        && Objects.equals(sessionDto.getImei(), clientInfo.getImei())
                        && Objects.equals(sessionDto.getClientType(), clientInfo.getClientType());
    }



}


