package cn.ctrlcv.im.codec.pack.friendship;

import lombok.Data;

/**
 * Class Name: AddFriendPack
 * Class Description: 添加好友通知报文
 *
 * @author liujm
 * @date 2023-03-19
 */
@Data
public class AddFriendPack {

    /**
     * 发起方id
     */
    private String fromId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 接收方id
     */
    private String toId;
    /**
     * 好友来源
     */
    private String addSource;
    /**
     * 添加好友时的描述信息（用于打招呼）
     */
    private String addWording;

    private Long sequence;

}
