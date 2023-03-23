package cn.ctrlcv.im.messagestore.model;

import cn.ctrlcv.im.common.model.message.MessageContent;
import cn.ctrlcv.im.messagestore.dao.ImMessageBodyEntity;
import lombok.Data;

/**
 * Class Name: DoStoreP2pMessageDTO
 * Class Description: 持久化私聊消息DTO
 *
 * @author liujm
 * @date 2023-03-23
 */
@Data
public class DoStoreP2pMessageDTO {

    private MessageContent messageContent;

    private ImMessageBodyEntity imMessageBodyEntity;

}
