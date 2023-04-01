package cn.ctrlcv.im.serve.utils;

/**
 * Class Name: ConversationIdGenerate
 * Class Description: 单聊或群聊序号id生成器
 *
 * @author liujm
 * @date 2023-04-01
 */
public class ConversationIdGenerate {

    /**
     * 生成P2P会话id.
     * <p>比较两个id的大小，大的id在前，小的id在后，用“|”连接</p>
     *
     * @param fromId 发送者id
     * @param toId 接收者id
     * @return 会话id
     */
    public static String generateP2PConversationId(String fromId, String toId) {
        // 校验参数
        if (fromId == null || toId == null) {
            throw new IllegalArgumentException("fromId or toId is null");
        }
        if (fromId.compareTo(toId) > 0) {
            return fromId + "|" + toId;
        } else {
            return toId + "|" + fromId;
        }
    }

}
