package cn.ctrlcv.im.codec.pack;

import lombok.Data;

import java.io.Serializable;

/**
 * Class Name: MessagePack
 * Class Description: 消息服务发送给tcp的包体,tcp再根据改包体解析成Message发给客户端
 *
 * @author liujm
 * @date 2023-03-15
 */
@Data
public class MessagePack<T> implements Serializable {

    private String userId;

    private Integer appId;

    /**
     * 接收方
     */
    private String toId;

    /**
     * 客户端标识
     */
    private int clientType;

    /**
     * 消息ID
     */
    private String messageId;

    /**
     * 客户端设备唯一标识
     */
    private String imei;

    private Integer command;

    /**
     * 业务数据对象，如果是聊天消息则不需要解析直接透传
     */
    private T data;

}
