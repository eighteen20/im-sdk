package cn.ctrlcv.im.serve.message.dao;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
    * 群组消息，消息关系存储
    */
@Data
@TableName(value = "im_group_message_history")
public class ImGroupMessageHistory implements Serializable {
    /**
     * 应用ID
     */
    @TableId(value = "app_id", type = IdType.INPUT)
    private Integer appId;

    /**
     * 群组ID
     */
    @TableId(value = "group_id", type = IdType.INPUT)
    private String groupId;

    /**
     * 消息主键
     */
    @TableId(value = "message_key", type = IdType.INPUT)
    private Long messageKey;

    /**
     * 消息发送人
     */
    @TableField(value = "from_id")
    private String fromId;

    /**
     * 数据库插入时间
     */
    @TableField(value = "create_time")
    private Long createTime;

    /**
     * 序号
     */
    @TableField(value = "`sequence`")
    private Long sequence;

    /**
     * 消息随机数
     */
    @TableField(value = "message_random")
    private Integer messageRandom;

    /**
     * 客户端发送时间
     */
    @TableField(value = "message_time")
    private Long messageTime;

    private static final long serialVersionUID = 1L;

    public static final String COL_APP_ID = "app_id";

    public static final String COL_GROUP_ID = "group_id";

    public static final String COL_MESSAGE_KEY = "message_key";

    public static final String COL_FROM_ID = "from_id";

    public static final String COL_CREATE_TIME = "create_time";

    public static final String COL_SEQUENCE = "sequence";

    public static final String COL_MESSAGE_RANDOM = "message_random";

    public static final String COL_MESSAGE_TIME = "message_time";
}