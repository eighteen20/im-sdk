package cn.ctrlcv.im.serve.message.dao;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import lombok.Data;

/**
 * 单聊消息，消息内容存储
 * @author ljm19
 */
@Data
@TableName(value = "im_message_body")
public class ImMessageBodyEntity implements Serializable {
    /**
     * 主键，消息的唯一标志
     */
    @TableId(value = "message_key", type = IdType.INPUT)
    private Long messageKey;

    /**
     * 应用ID
     */
    @TableField(value = "app_id")
    private Integer appId;

    /**
     * 消息内容
     */
    @TableField(value = "message_body")
    private String messageBody;

    /**
     * 预留字段，给消息加密的key
     */
    @TableField(value = "security_key")
    private String securityKey;

    /**
     * 客户端发送消息的时间
     */
    @TableField(value = "message_time")
    private Long messageTime;

    /**
     * 服务器保存消息的时间
     */
    @TableField(value = "create_time")
    private Long createTime;

    @TableField(value = "extra")
    private String extra;

    /**
     * 删除标志
     */
    @TableField(value = "del_flag")
    private Integer delFlag;

    private static final long serialVersionUID = 1L;

    public static final String COL_MESSAGE_KEY = "message_key";

    public static final String COL_APP_ID = "app_id";

    public static final String COL_MESSAGE_BODY = "message_body";

    public static final String COL_SECURITY_KEY = "security_key";

    public static final String COL_MESSAGE_TIME = "message_time";

    public static final String COL_CREATE_TIME = "create_time";

    public static final String COL_EXTRA = "extra";

    public static final String COL_DEL_FLAG = "del_flag";
}