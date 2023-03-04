package cn.ctrlcv.im.serve.group.model.request;

import cn.ctrlcv.im.common.model.RequestBase;
import cn.ctrlcv.im.serve.group.model.dto.GroupMemberDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * Class Name: CreateGroupReq
 * Class Description: 创建群组接口的请求参数
 *
 * @author liujm
 * @date 2023-03-04
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CreateGroupReq extends RequestBase {

    private String groupId;
    //群主id
    private String ownerId;

    /**
     * 群类型 1私有群（类似微信） 2公开群(类似qq）
     */
    private Integer groupType;

    private String groupName;

    /**
     *
     */
    private Integer mute;

    /**
     * 加入群权限，0 所有人可以加入；1 群成员可以拉人；2 群管理员或群组可以拉人。
     */
    private Integer applyJoinType;

    /**
     * 群简介
     */
    private String introduction;

    /**
     * 群公告
     */
    private String notification;

    /**
     * 群头像
     */
    private String photo;

    private Integer maxMemberCount;

    private List<GroupMemberDTO> member;

    private String extra;

}
