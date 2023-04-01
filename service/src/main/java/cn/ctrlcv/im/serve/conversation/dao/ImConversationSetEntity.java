package cn.ctrlcv.im.serve.conversation.dao;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import com.github.jeffreyning.mybatisplus.anno.MppMultiId;
import lombok.Data;

/**
 * 会话记录
 */
@Data
@TableName(value = "im_conversation_set")
public class ImConversationSetEntity implements Serializable {
    /**
     * 应用ID
     */
    @MppMultiId
    private Integer appId;

    /**
     * 会话ID：会话类型_fromId_toId
     */
    @MppMultiId
    private String conversationId;

    /**
     * 0 单聊 1群聊 2机器人 3公众号
     */
    @TableField(value = "conversation_type")
    private Integer conversationType;

    /**
     * 发起方ID
     */
    @TableField(value = "from_id")
    private String fromId;

    /**
     * 接收方ID
     */
    @TableField(value = "to_id")
    private String toId;

    /**
     * 是否免打扰 1免打扰
     */
    @TableField(value = "is_mute")
    private Integer isMute;

    /**
     * 是否置顶 1置顶
     */
    @TableField(value = "is_top")
    private Integer isTop;

    /**
     * sequence
     */
    @TableField(value = "`sequence`")
    private Long sequence;

    /**
     * 已读到了会话中哪条消息的seq
     */
    @TableField(value = "read_sequence")
    private Long readSequence;

    private static final long serialVersionUID = 1L;

    public static final String COL_APP_ID = "app_id";

    public static final String COL_CONVERSATION_ID = "conversation_id";

    public static final String COL_CONVERSATION_TYPE = "conversation_type";

    public static final String COL_FROM_ID = "from_id";

    public static final String COL_TO_ID = "to_id";

    public static final String COL_IS_MUTE = "is_mute";

    public static final String COL_IS_TOP = "is_top";

    public static final String COL_SEQUENCE = "sequence";

    public static final String COL_READ_SEQUENCE = "read_sequence";
}