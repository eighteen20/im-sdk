package cn.ctrlcv.im.serve.message.dao;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import com.github.jeffreyning.mybatisplus.anno.MppMultiId;
import lombok.Data;

/**
    * 单聊消息，消息关系存储
    */
@Data
@TableName(value = "im_message_history")
public class ImMessageHistoryEntity implements Serializable {
    /**
     * 应用ID
     */
    @MppMultiId
    private Integer appId;

    /**
     * 这条消息归属人(写扩散方式存)
     */
    @MppMultiId
    private String ownerId;

    /**
     * 聊天消息的唯一标识，对应im_message_body主键
     */
    @MppMultiId
    private Long messageKey;

    /**
     * 发送方
     */
    @TableField(value = "from_id")
    private String fromId;

    /**
     * 接收方
     */
    @TableField(value = "to_id")
    private String toId;

    /**
     * 服务端保存数据的时间
     */
    @TableField(value = "create_time")
    private Long createTime;

    /**
     * 序列号
     */
    @TableField(value = "`sequence`")
    private Long sequence;

    /**
     * 随机数
     */
    @TableField(value = "message_random")
    private Integer messageRandom;

    /**
     * 客户端发送消息的时间
     */
    @TableField(value = "message_time")
    private Long messageTime;

    private static final long serialVersionUID = 1L;

    public static final String COL_APP_ID = "app_id";

    public static final String COL_OWNER_ID = "owner_id";

    public static final String COL_MESSAGE_KEY = "message_key";

    public static final String COL_FROM_ID = "from_id";

    public static final String COL_TO_ID = "to_id";

    public static final String COL_CREATE_TIME = "create_time";

    public static final String COL_SEQUENCE = "sequence";

    public static final String COL_MESSAGE_RANDOM = "message_random";

    public static final String COL_MESSAGE_TIME = "message_time";
}