package cn.ctrlcv.im.serve.utils;

import cn.ctrlcv.im.codec.pack.group.AddGroupMemberPack;
import cn.ctrlcv.im.codec.pack.group.RemoveGroupMemberPack;
import cn.ctrlcv.im.codec.pack.group.UpdateGroupMemberPack;
import cn.ctrlcv.im.common.enums.ClientTypeEnum;
import cn.ctrlcv.im.common.enums.command.Command;
import cn.ctrlcv.im.common.enums.command.GroupEventCommand;
import cn.ctrlcv.im.common.model.ClientInfo;
import cn.ctrlcv.im.serve.group.model.dto.GroupMemberDTO;
import cn.ctrlcv.im.serve.group.service.IGroupMemberService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * Class Name: GroupMessageProducer
 * Class Description: 群消息投递工具，向群成员发送消息
 *
 * @author liujm
 * @date 2023-03-19
 */
@Component
public class GroupMessageProducer {

    @Resource
    private MessageProducer messageProducer;

    @Resource
    private IGroupMemberService groupMemberService;


    /**
     * 投递消息给群成员
     *
     * @param userId     用户ID
     * @param command    投递指令
     * @param msg        消息内容
     * @param clientInfo {@link ClientInfo}
     */
    public void producer(String userId, Command command, Object msg, ClientInfo clientInfo) {
        JSONObject object = (JSONObject) JSONObject.toJSON(msg);
        String groupId = object.getString("groupId");
        // 获取所有群成员
        List<String> groupMemberIds = groupMemberService.getGroupMemberId(groupId, clientInfo.getAppId());

        if (command.equals(GroupEventCommand.ADDED_MEMBER)) {
            //发送给管理员和被加入人本身
            List<GroupMemberDTO> groupManager = groupMemberService.getGroupManager(groupId, clientInfo.getAppId());
            AddGroupMemberPack addGroupMemberPack
                    = object.toJavaObject(AddGroupMemberPack.class);
            List<String> members = addGroupMemberPack.getMembers();
            for (GroupMemberDTO groupMemberDto : groupManager) {
                if (clientInfo.getClientType() != ClientTypeEnum.WEBAPI.getCode() && groupMemberDto.getMemberId().equals(userId)) {
                    messageProducer.sendToUserExceptClient(groupMemberDto.getMemberId(), command, msg, clientInfo);
                } else {
                    messageProducer.sendToUser(groupMemberDto.getMemberId(), command, msg, clientInfo.getAppId());
                }
            }
            for (String member : members) {
                if (clientInfo.getClientType() != ClientTypeEnum.WEBAPI.getCode() && member.equals(userId)) {
                    messageProducer.sendToUserExceptClient(member, command, msg, clientInfo);
                } else {
                    messageProducer.sendToUser(member, command, msg, clientInfo.getAppId());
                }
            }
        } else if (command.equals(GroupEventCommand.DELETED_MEMBER)) {
            // 发送给所有成员员和被删除人本身
            RemoveGroupMemberPack pack = object.toJavaObject(RemoveGroupMemberPack.class);
            String member = pack.getMember();
            List<String> members = groupMemberService.getGroupMemberId(groupId, clientInfo.getAppId());
            members.add(member);
            for (String memberId : members) {
                if (clientInfo.getClientType() != ClientTypeEnum.WEBAPI.getCode() && member.equals(userId)) {
                    messageProducer.sendToUserExceptClient(memberId, command, msg, clientInfo);
                } else {
                    messageProducer.sendToUser(memberId, command, msg, clientInfo.getAppId());
                }
            }
        } else if (command.equals(GroupEventCommand.UPDATED_MEMBER)) {
            UpdateGroupMemberPack pack = object.toJavaObject(UpdateGroupMemberPack.class);
            String memberId = pack.getMemberId();
            List<GroupMemberDTO> groupManager = groupMemberService.getGroupManager(groupId, clientInfo.getAppId());
            GroupMemberDTO groupMemberDto = new GroupMemberDTO();
            groupMemberDto.setMemberId(memberId);
            groupManager.add(groupMemberDto);
            for (GroupMemberDTO member : groupManager) {
                if (clientInfo.getClientType() != ClientTypeEnum.WEBAPI.getCode() && memberId.equals(userId)) {
                    messageProducer.sendToUserExceptClient(member.getMemberId(), command, msg, clientInfo);
                } else {
                    messageProducer.sendToUser(member.getMemberId(), command, msg, clientInfo.getAppId());
                }
            }
        } else {
            for (String memberId : groupMemberIds) {
                if (clientInfo.getClientType() != null && clientInfo.getClientType() !=
                        ClientTypeEnum.WEBAPI.getCode() && memberId.equals(userId)) {
                    messageProducer.sendToUserExceptClient(memberId, command,
                            msg, clientInfo);
                } else {
                    messageProducer.sendToUser(memberId, command, msg, clientInfo.getAppId());
                }
            }
        }
    }
}
