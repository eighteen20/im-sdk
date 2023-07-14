package cn.ctrlcv.im.serve.message.service;

import cn.ctrlcv.im.common.model.ResponseVO;
import cn.ctrlcv.im.common.config.ImConfig;
import cn.ctrlcv.im.common.enums.*;
import cn.ctrlcv.im.serve.friendship.dao.ImFriendshipEntity;
import cn.ctrlcv.im.serve.friendship.model.request.GetRelationReq;
import cn.ctrlcv.im.serve.friendship.service.IFriendshipService;
import cn.ctrlcv.im.serve.group.dao.ImGroupEntity;
import cn.ctrlcv.im.serve.group.model.resp.GetRoleInGroupResp;
import cn.ctrlcv.im.serve.group.service.IGroupMemberService;
import cn.ctrlcv.im.serve.group.service.IGroupService;
import cn.ctrlcv.im.serve.user.dao.ImUserDataEntity;
import cn.ctrlcv.im.serve.user.service.IUserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Class Name: CheckSendMessageService
 * Class Description: 发消息前的校验
 *
 * @author liujm
 * @date 2023-03-21
 */
@Service
public class CheckSendMessageService {

    @Resource
    private IUserService userService;

    @Resource
    private IFriendshipService friendshipService;

    @Resource
    private IGroupService groupService;

    @Resource
    private IGroupMemberService groupMemberService;

    @Resource
    private ImConfig imConfig;


    /**
     * 检查用户是否被禁言或者禁用
     *
     * @param fromId 发送方
     * @param appId  应用ID
     * @return responseVO {@link ResponseVO}
     */
    public ResponseVO checkUserForbiddenOrSilent(String fromId, Integer appId) {
        ResponseVO<ImUserDataEntity> singleUserInfo = userService.getSingleUserInfo(fromId, appId);
        if (!singleUserInfo.isOk()) {
            return singleUserInfo;
        }

        ImUserDataEntity userDataEntity = singleUserInfo.getData();
        if (userDataEntity.getSilentFlag() == UserSilentFlagEnum.MUTE.getCode()) {
            return ResponseVO.errorResponse(MessageErrorCode.FROMER_IS_MUTE);
        }
        if (userDataEntity.getForbiddenFlag() == UserForbiddenFlagEnum.FORBIDDEN.getCode()) {
            return ResponseVO.errorResponse(MessageErrorCode.FROMER_IS_FORBIDDEN);
        }


        return ResponseVO.successResponse();
    }

    /**
     * 校验是否是好友关系
     *
     * @param fromId 发送方
     * @param toId   接收方
     * @param appId  应用ID
     * @return responseVO {@link ResponseVO}
     */
    public ResponseVO checkIsFriend(String fromId, String toId, Integer appId) {
        if (imConfig.isSendMessageCheckFriend()) {
            GetRelationReq fromReq = new GetRelationReq();
            fromReq.setFromId(fromId);
            fromReq.setToId(toId);
            fromReq.setAppId(appId);
            ResponseVO<ImFriendshipEntity> fromRelation = friendshipService.getRelation(fromReq);
            if (!fromRelation.isOk()) {
                return fromRelation;
            }
            GetRelationReq toReq = new GetRelationReq();
            fromReq.setFromId(toId);
            fromReq.setToId(fromId);
            fromReq.setAppId(appId);
            ResponseVO<ImFriendshipEntity> toRelation = friendshipService.getRelation(toReq);
            if (!toRelation.isOk()) {
                return toRelation;
            }

            if (FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode() != fromRelation.getData().getStatus()) {
                return ResponseVO.errorResponse(FriendShipErrorCodeEnum.FRIEND_IS_DELETED);
            }

            if (FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode() != toRelation.getData().getStatus()) {
                return ResponseVO.errorResponse(FriendShipErrorCodeEnum.FRIEND_IS_DELETED);
            }

            if (imConfig.isSendMessageCheckBlack()) {
                if (FriendShipStatusEnum.BLACK_STATUS_NORMAL.getCode() != fromRelation.getData().getBlack()) {
                    return ResponseVO.errorResponse(FriendShipErrorCodeEnum.FRIEND_IS_BLACK);
                }

                if (FriendShipStatusEnum.BLACK_STATUS_NORMAL.getCode() != toRelation.getData().getBlack()) {
                    return ResponseVO.errorResponse(FriendShipErrorCodeEnum.TARGET_IS_BLACK_YOU);
                }
            }
        }
        return ResponseVO.successResponse();

    }

    /**
     * 在群聊消息发送前，进行校验。
     * 1.校验用户是否被禁言或者禁用
     * 2.校验群是否存在
     * 3.校验用户是否在群里
     * 4.校验群是否被禁言，如果是全员禁言，只有群主和管理员可以发言
     * 5.校验用户是否被禁言
     *
     * @param fromId  发送方
     * @param groupId 群ID
     * @param appId   应用ID
     * @return responseVO {@link ResponseVO}
     */
    public ResponseVO checkGroupMessage(String fromId, String groupId, Integer appId) {
        ResponseVO checkUserForbiddenOrSilent = checkUserForbiddenOrSilent(fromId, appId);
        if (!checkUserForbiddenOrSilent.isOk()) {
            return checkUserForbiddenOrSilent;
        }

        ResponseVO<ImGroupEntity> groupInfo = groupService.getGroup(groupId, appId);
        if (!groupInfo.isOk()) {
            return groupInfo;
        }

        ResponseVO<GetRoleInGroupResp> roleInGroupOne = groupMemberService.getRoleInGroupOne(groupId, fromId, appId);
        if (!roleInGroupOne.isOk()) {
            return roleInGroupOne;
        }

        GetRoleInGroupResp data = roleInGroupOne.getData();
        ImGroupEntity groupEntity = groupInfo.getData();

        if (groupEntity.getMute() == GroupMuteTypeEnum.MUTE.getCode()) {
            if (data.getRole() != GroupMemberRoleEnum.MANAGER.getCode() || data.getRole() != GroupMemberRoleEnum.OWNER.getCode()) {
                return ResponseVO.errorResponse(GroupErrorCodeEnum.THIS_GROUP_IS_MUTE);
            }
        }

        if (data.getSpeakDate() != null && data.getSpeakDate() > System.currentTimeMillis()) {
            return ResponseVO.errorResponse(GroupErrorCodeEnum.GROUP_MEMBER_IS_SPEAK);
        }

        return ResponseVO.successResponse();
    }

}