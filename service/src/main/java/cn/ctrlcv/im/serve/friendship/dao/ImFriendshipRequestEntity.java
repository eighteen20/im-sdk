package cn.ctrlcv.im.serve.friendship.dao;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
    * 好友添加申请表
 * @author ljm19
 */
@Data
@TableName(value = "im_friendship_request")
public class ImFriendshipRequestEntity implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    /**
     * 用户ID
     */
    @TableField(value = "from_id")
    private String fromId;

    /**
     * 待申请添加的好友ID
     */
    @TableField(value = "to_id")
    private String toId;

    /**
     * 应用ID
     */
    @TableField(value = "app_id")
    private Integer appId;

    /**
     * 是否已读 0-未读；1-已读
     */
    @TableField(value = "read_status")
    private Integer readStatus;

    /**
     * 好友申请留言
     */
    @TableField(value = "add_wording")
    private String addWording;

    /**
     * 备注
     */
    @TableField(value = "remark")
    private String remark;

    /**
     * 审批结果
     */
    @TableField(value = "approve_status")
    private Integer approveStatus;

    @TableField(value = "create_time")
    private Long createTime;

    @TableField(value = "update_time")
    private Long updateTime;

    @TableField(value = "`sequence`")
    private Long sequence;

    /**
     * 添加来源
     */
    @TableField(value = "add_source")
    private String addSource;

    private static final long serialVersionUID = 1L;

    public static final String COL_ID = "id";

    public static final String COL_FROM_ID = "from_id";

    public static final String COL_TO_ID = "to_id";

    public static final String COL_APP_ID = "app_id";

    public static final String COL_READ_STATUS = "read_status";

    public static final String COL_ADD_WORDING = "add_wording";

    public static final String COL_REMARK = "remark";

    public static final String COL_APPROVE_STATUS = "approve_status";

    public static final String COL_CREATE_TIME = "create_time";

    public static final String COL_UPDATE_TIME = "update_time";

    public static final String COL_SEQUENCE = "sequence";

    public static final String COL_ADD_SOURCE = "add_source";
}