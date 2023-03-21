package cn.ctrlcv.im.codec.pack.group;

import lombok.Data;

/**
 * Class Name: GroupMemberSpeakPack
 * Class Description: 群成员禁言通知报文
 *
 * @author liujm
 * @date 2023-03-21
 */
@Data
public class GroupMemberSpeakPack {

        private String groupId;

        private String memberId;

        private Long speakDate;
}
