package cn.ctrlcv.im.common.model.message;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Class Name: GroupChatMessageContent
 * Class Description: 群聊消息内容
 *
 * @author liujm
 * @date 2023-03-22
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GroupChatMessageContent extends MessageContent {

        private String groupId;

        private String memberId;
}
