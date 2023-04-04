package cn.ctrlcv.im.codec.pack.conversation;

import lombok.Data;

/**
 * Class Name: UpdateConversationPack
 * Class Description: 更新会话包
 *
 * @author liujm
 * @date 2023-04-04
 */
@Data
public class UpdateConversationPack {

    private String conversationId;

    private Integer isMute;

    private Integer isTop;

    private Integer conversationType;


}
