package cn.ctrlcv.im.serve.friendship.service.impl;

import cn.ctrlcv.im.common.ResponseVO;
import cn.ctrlcv.im.serve.friendship.model.request.FriendDTO;
import cn.ctrlcv.im.serve.friendship.service.IFriendshipRequestService;

/**
 * Class Name: FriendshipRequestImpl
 * Class Description: 好友请求相关接口实现
 *
 * @author liujm
 * @date 2023-02-10
 */
public class FriendshipRequestImpl implements IFriendshipRequestService {
    @Override
    public ResponseVO<?> addFriendshipRequest(String fromId, FriendDTO dto, Integer appId) {
        return null;
    }
}
