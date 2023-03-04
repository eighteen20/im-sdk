package cn.ctrlcv.im.serve.group.model.dto;

import lombok.Data;

/**
 * Class Name: GroupMemberDTO
 * Class Description: 批量导入群成员需要的值
 *
 * @author liujm
 * @date 2023-03-02
 */
@Data
public class GroupMemberDTO {

    /**
     * 群ID
     */
    private String groupId;

    /**
     * 群成员ID
     */
    private String memberId;

    /**
     * 禁言到期时间
     */
    private Long speakDate;

    /**
     * 群成员类型：0-普通成员, 1-管理员, 2-群主, 3-已退出
     */
    private Integer role;

    /**
     * 群昵称
     */
    private String alias;

    /**
     * 加入时间
     */
    private Long joinTime;

    /**
     * 离开时间
     */
    private Long leaveTime;

    /**
     * 加入方式
     */
    private String joinType;

    private String extra;


}
