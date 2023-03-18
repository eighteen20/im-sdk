package cn.ctrlcv.im.serve.utils;

import cn.ctrlcv.im.common.ClientTypeEnum;
import cn.ctrlcv.im.common.ResponseVO;
import cn.ctrlcv.im.common.enums.command.Command;
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
        JSONObject o = (JSONObject) JSONObject.toJSON(msg);
        String groupId = o.getString("groupId");
        // 获取所有群成员
        List<String> groupMemberIds = groupMemberService.getGroupMemberId(groupId, clientInfo.getAppId());

        for (String memberId : groupMemberIds) {
            // 如果不是webapi，给自己发消息
            if (clientInfo.getClientType() != null && clientInfo.getClientType() !=
                    ClientTypeEnum.WEBAPI.getCode() && memberId.equals(userId)) {
                messageProducer.sendToUserExceptClient(memberId, command, msg, clientInfo);
            } else {
                messageProducer.sendToUser(memberId, command, msg, clientInfo.getAppId());
            }
        }
    }
}
