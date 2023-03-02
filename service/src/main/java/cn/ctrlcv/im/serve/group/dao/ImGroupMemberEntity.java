package cn.ctrlcv.im.serve.group.dao;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
    * 群成员信息表
    */
@Data
@TableName(value = "im_group_member")
public class ImGroupMemberEntity implements Serializable {
    /**
     * 群成员主键
     */
    @TableId(value = "group_member_id", type = IdType.INPUT)
    private Long groupMemberId;

    /**
     * 应用ID
     */
    @TableField(value = "app_id")
    private Integer appId;

    /**
     * 群ID
     */
    @TableField(value = "group_id")
    private String groupId;

    /**
     * 群成员ID
     */
    @TableField(value = "member_id")
    private String memberId;

    /**
     * 禁言到期时间
     */
    @TableField(value = "speak_date")
    private Long speakDate;

    /**
     * 群成员类型：0-普通成员, 1-管理员, 2-群主, 3-已退出
     */
    @TableField(value = "`role`")
    private Byte role;

    /**
     * 群昵称
     */
    @TableField(value = "`alias`")
    private String alias;

    /**
     * 加入时间
     */
    @TableField(value = "join_time")
    private Long joinTime;

    /**
     * 离开时间
     */
    @TableField(value = "leave_time")
    private Long leaveTime;

    /**
     * 加入方式
     */
    @TableField(value = "join_type")
    private String joinType;

    @TableField(value = "extra")
    private String extra;

    private static final long serialVersionUID = 1L;

    public static final String COL_GROUP_MEMBER_ID = "group_member_id";

    public static final String COL_APP_ID = "app_id";

    public static final String COL_GROUP_ID = "group_id";

    public static final String COL_MEMBER_ID = "member_id";

    public static final String COL_SPEAK_DATE = "speak_date";

    public static final String COL_ROLE = "role";

    public static final String COL_ALIAS = "alias";

    public static final String COL_JOIN_TIME = "join_time";

    public static final String COL_LEAVE_TIME = "leave_time";

    public static final String COL_JOIN_TYPE = "join_type";

    public static final String COL_EXTRA = "extra";
}