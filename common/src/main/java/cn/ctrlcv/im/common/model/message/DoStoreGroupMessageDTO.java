package cn.ctrlcv.im.common.model.message;

import lombok.Data;

/**
 * Class Name: DoStoreGroupMessageDTO
 * Class Description: 持久化群聊消息DTO
 *
 * @author liujm
 * @date 2023-03-23
 */
@Data
public class DoStoreGroupMessageDTO {

        private GroupChatMessageContent messageContent;

        private ImMessageBody messageBody;
}
