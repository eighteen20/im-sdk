package cn.ctrlcv.im.serve.friendship.dao.mapper;

import cn.ctrlcv.im.serve.friendship.dao.ImFriendshipEntity;
import cn.ctrlcv.im.serve.friendship.model.request.CheckFriendShipReq;
import cn.ctrlcv.im.serve.friendship.model.response.CheckFriendShipResp;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * 好友关系数据层
 *
 * @author ljm19
 */
public interface ImFriendshipMapper extends BaseMapper<ImFriendshipEntity> {

    /**
     * 查询单向的好友关系
     *
     * @param req {@link CheckFriendShipReq}
     * @return {@link List}<{@link CheckFriendShipResp}>
     */
    List<CheckFriendShipResp> getSingleFriendship(CheckFriendShipReq req);


    /**
     * 查询双向好友关系
     *
     * @param req {@link CheckFriendShipReq}
     * @return {@link List}<{@link CheckFriendShipResp}>
     */
    List<CheckFriendShipResp> getBothFriendship(CheckFriendShipReq req);

}