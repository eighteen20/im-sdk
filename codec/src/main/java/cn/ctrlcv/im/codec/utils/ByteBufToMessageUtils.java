package cn.ctrlcv.im.codec.utils;

import cn.ctrlcv.im.codec.proto.Message;
import cn.ctrlcv.im.codec.proto.MessageHeader;
import cn.ctrlcv.im.common.enums.MessageTypeEnum;
import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;

/**
 * Class Name: ByteBufToMessageUtils
 * Class Description: 根据私有协议, 将ByteBuf转化为Message实体
 *
 * <br>
 * <p>
 * <p>私有协议设计：</p>
 * <p>请求头 + IMEI号 + 请求体</p>
 * <p>请求头：指令 + 版本 + clientType + 消息解析类型 + appId + IMEI号长度 + 请求体长度 【每个信息均为4字节】</p>
 * </p>
 *
 * @author liujm
 * @date 2023-03-13
 */
public class ByteBufToMessageUtils {

    public static Message transition(ByteBuf in){

        /*
         * 获取command
         */
        int command = in.readInt();

        /*
         * 获取version
         */
        int version = in.readInt();

        /*
         * 获取clientType
         */
        int clientType = in.readInt();

        /*
         * 获取messageType（消息解析类型）
         */
        int messageType = in.readInt();

        /*
         * 获取appId
         */
        int appId = in.readInt();

        /*
         * 获取IMEI号长度
         */
        int imeiLength = in.readInt();

        /*
         * 获取bodyLen
         */
        int bodyLength = in.readInt();

        if (in.readableBytes() < bodyLength + imeiLength) {
            // 数据长度不够，重置读索引
            in.resetReaderIndex();
            return null;
        }

        /*
         * 获取IMEI号
         */
        byte[] imeiData = new byte[imeiLength];
        in.readBytes(imeiData);
        String imei = new String(imeiData);

        /*
         * 获取消息体
         */
        byte[] bodyDate = new byte[bodyLength];
        in.readBytes(bodyDate);
        String body = new String(bodyDate);


        // ByteBuf 转为 协议实体类
        MessageHeader messageHeader = new MessageHeader();
        messageHeader.setAppId(appId);
        messageHeader.setClientType(clientType);
        messageHeader.setCommand(command);
        messageHeader.setBodyLength(bodyLength);
        messageHeader.setVersion(version);
        messageHeader.setMessageType(messageType);
        messageHeader.setImeiLength(imeiLength);
        messageHeader.setImei(imei);

        Message message = new Message();
        message.setMessageHeader(messageHeader);

        if (messageType == MessageTypeEnum.JSON.getType()) {
            JSONObject parse = JSONObject.parseObject(body);
            message.setMessagePack(parse);
        }

        in.markReaderIndex();
        return message;
    }

}
