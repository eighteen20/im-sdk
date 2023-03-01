package cn.ctrlcv.im.serve.friendship.dao;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
    * 好友分组信息表
    */
@Data
@TableName(value = "im_friendship_group")
public class ImFriendshipGroupEntity implements Serializable {
    /**
     * 主键，组ID
     */
    @TableId(value = "group_id", type = IdType.INPUT)
    private Long groupId;

    /**
     * 用户ID
     */
    @TableField(value = "from_id")
    private String fromId;

    /**
     * 应用ID
     */
    @TableField(value = "app_id")
    private Integer appId;

    /**
     * 分组名称
     */
    @TableField(value = "group_name")
    private String groupName;

    @TableField(value = "`sequence`")
    private Long sequence;

    @TableField(value = "create_time")
    private Long createTime;

    @TableField(value = "update_time")
    private Long updateTime;

    /**
     * 删除标志
     */
    @TableField(value = "del_flag")
    private Integer delFlag;

    private static final long serialVersionUID = 1L;

    public static final String COL_GROUP_ID = "group_id";

    public static final String COL_FROM_ID = "from_id";

    public static final String COL_APP_ID = "app_id";

    public static final String COL_GROUP_NAME = "group_name";

    public static final String COL_SEQUENCE = "sequence";

    public static final String COL_CREATE_TIME = "create_time";

    public static final String COL_UPDATE_TIME = "update_time";

    public static final String COL_DEL_FLAG = "del_flag";
}