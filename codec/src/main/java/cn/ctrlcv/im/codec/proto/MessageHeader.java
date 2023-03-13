package cn.ctrlcv.im.codec.proto;

import cn.ctrlcv.im.common.enums.MessageTypeEnum;
import lombok.Data;

/**
 * Class Name: MessageHeader
 * Class Description: 私有协议-消息头
 *
 * @author liujm
 * @date 2023-03-13
 */
@Data
public class MessageHeader {

    /**
     * 获取command
     */
    private int command;

    /**
     * 获取version
     */
    private int version;

    /**
     * 获取clientType
     */
    private int clientType;

    /**
     * 获取messageType（消息解析类型）
     */
    private int messageType;

    /**
     * 获取appId
     */
    private int appId;

    /**
     * 获取IMEI号长度
     */
    private int imeiLength = MessageTypeEnum.JSON.getType();

    /**
     * IMEI号
     */
    private String imei;

    /**
     * 获取bodyLen
     */
    private int bodyLength;
}
