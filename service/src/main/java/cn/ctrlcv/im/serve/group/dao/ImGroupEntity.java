package cn.ctrlcv.im.serve.group.dao;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
    * 群信息表
    */
@Data
@TableName(value = "im_group")
public class ImGroupEntity implements Serializable {
    /**
     * 主键，群ID
     */
    @TableId(value = "group_id", type = IdType.INPUT)
    private String groupId;

    /**
     * 应用ID
     */
    @TableId(value = "app_id", type = IdType.INPUT)
    private Integer appId;

    /**
     * 群主ID
     */
    @TableField(value = "owner_id")
    private String ownerId;

    /**
     * 群类型，1-私有群，2-公有群
     */
    @TableField(value = "group_type")
    private Integer groupType;

    /**
     * 群名称
     */
    @TableField(value = "group_name")
    private String groupName;

    /**
     * 是否开启禁言, 1-禁言
     */
    @TableField(value = "mute")
    private Boolean mute;

    /**
     * 群状态，1-正常，0-解散
     */
    @TableField(value = "`status`")
    private Integer status;

    /**
     * 申请加群选项：0-禁止任何人申请, 1-需要群主或管理员审批, 2-无需审批直接加入
     */
    @TableField(value = "apply_join_type")
    private Integer applyJoinType;

    /**
     * 群简介
     */
    @TableField(value = "introduction")
    private String introduction;

    /**
     * 群公告
     */
    @TableField(value = "notification")
    private String notification;

    /**
     * 头像
     */
    @TableField(value = "photo")
    private String photo;

    /**
     * 最大群成员人数
     */
    @TableField(value = "max_member_count")
    private Integer maxMemberCount;

    /**
     * seq
     */
    @TableField(value = "`sequence`")
    private Long sequence;

    @TableField(value = "create_time")
    private Long createTime;

    @TableField(value = "update_time")
    private Long updateTime;

    @TableField(value = "extra")
    private String extra;

    private static final long serialVersionUID = 1L;

    public static final String COL_GROUP_ID = "group_id";

    public static final String COL_APP_ID = "app_id";

    public static final String COL_OWNER_ID = "owner_id";

    public static final String COL_GROUP_TYPE = "group_type";

    public static final String COL_GROUP_NAME = "group_name";

    public static final String COL_MUTE = "mute";

    public static final String COL_STATUS = "status";

    public static final String COL_APPLY_JOIN_TYPE = "apply_join_type";

    public static final String COL_INTRODUCTION = "introduction";

    public static final String COL_NOTIFICATION = "notification";

    public static final String COL_PHOTO = "photo";

    public static final String COL_MAX_MEMBER_COUNT = "max_member_count";

    public static final String COL_SEQUENCE = "sequence";

    public static final String COL_CREATE_TIME = "create_time";

    public static final String COL_UPDATE_TIME = "update_time";

    public static final String COL_EXTRA = "extra";
}