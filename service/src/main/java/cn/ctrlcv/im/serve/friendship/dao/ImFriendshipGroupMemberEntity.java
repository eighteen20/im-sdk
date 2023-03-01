package cn.ctrlcv.im.serve.friendship.dao;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
    * 好友分组成员表
    */
@Data
@TableName(value = "im_friendship_group_member")
public class ImFriendshipGroupMemberEntity implements Serializable {
    /**
     * 分组ID
     */
    @TableId(value = "group_id", type = IdType.INPUT)
    private Long groupId;

    /**
     * 好友ID
     */
    @TableId(value = "to_id", type = IdType.INPUT)
    private String toId;

    private static final long serialVersionUID = 1L;

    public static final String COL_GROUP_ID = "group_id";

    public static final String COL_TO_ID = "to_id";
}