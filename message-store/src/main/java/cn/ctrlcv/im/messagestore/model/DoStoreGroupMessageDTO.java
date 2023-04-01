package cn.ctrlcv.im.messagestore.model;

import cn.ctrlcv.im.common.model.message.GroupChatMessageContent;
import cn.ctrlcv.im.common.model.message.MessageContent;
import cn.ctrlcv.im.messagestore.dao.ImMessageBodyEntity;
import lombok.Data;

/**
 * Class Name: DoStoreGroupMessageDTO
 * Class Description: 持久化群聊消息DTO
 *
 * @author liujm
 * @date 2023-04-01
 */
@Data
public class DoStoreGroupMessageDTO {

    private GroupChatMessageContent groupChatMessageContent;

    private ImMessageBodyEntity imMessageBodyEntity;

}
