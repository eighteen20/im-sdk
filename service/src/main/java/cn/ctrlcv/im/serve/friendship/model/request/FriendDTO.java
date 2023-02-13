package cn.ctrlcv.im.serve.friendship.model.request;

import lombok.Data;

/**
 * Class Name: FriendDto
 * Class Description: 添加好友需要的信息
 *
 * @author liujm
 * @date 2023-02-09
 */
@Data
public class FriendDTO {
    private String toId;

    private String remark;

    private String addSource;

    private String extra;

    private String addWording;
}
