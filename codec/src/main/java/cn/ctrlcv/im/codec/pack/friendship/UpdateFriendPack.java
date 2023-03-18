package cn.ctrlcv.im.codec.pack.friendship;

import lombok.Data;

/**
 * Class Name: UpdateFriendPack
 * Class Description: 修改好友通知报文
 *
 * @author liujm
 * @date 2023-03-19
 */
@Data
public class UpdateFriendPack {

    public String fromId;

    private String toId;

    private String remark;

    private Long sequence;
}
