package cn.ctrlcv.im.serve.friendship.dao.mapper;

import cn.ctrlcv.im.serve.friendship.dao.ImFriendshipEntity;
import cn.ctrlcv.im.serve.friendship.model.request.CheckFriendShipReq;
import cn.ctrlcv.im.serve.friendship.model.response.CheckFriendShipResp;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

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

    /**
     * 查询单向的黑名单关系
     *
     * @param req {@link CheckFriendShipReq}
     * @return {@link List}<{@link CheckFriendShipResp}>
     */
    List<CheckFriendShipResp> getSingleFriendShipBlack(CheckFriendShipReq req);


    /**
     * 查询双向的黑名单关系
     *
     * @param req {@link CheckFriendShipReq}
     * @return {@link List}<{@link CheckFriendShipResp}>
     */
    List<CheckFriendShipResp> getBothFriendShipBlack(CheckFriendShipReq req);

    /**
     * 查询最大的friend_sequence
     *
     * @param appId 应用ID
     * @param userId 用户ID
     * @return {@link Long}
     */
    @Select("select max(friend_sequence) from im_friendship where app_id = #{param1} and from_id = #{param2}")
    Long getMaxFriendSequence(Integer appId, String userId);
}