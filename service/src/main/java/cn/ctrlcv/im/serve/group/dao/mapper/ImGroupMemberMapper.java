package cn.ctrlcv.im.serve.group.dao.mapper;

import cn.ctrlcv.im.serve.group.dao.ImGroupMemberEntity;
import cn.ctrlcv.im.serve.group.model.dto.GroupMemberDTO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.lettuce.core.dynamic.annotation.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author ljm19
 */
public interface ImGroupMemberMapper extends BaseMapper<ImGroupMemberEntity> {

    /**
     * 获取群组成员列表
     *
     * @param appId 应用ID
     * @param groupId 群组ID
     * @return {@link List}<{@link GroupMemberDTO}>
     */
    List<GroupMemberDTO> getGroupMember(@Param("appId") Integer appId, @Param("groupId") String groupId);

    /**
     * 查询用户加入的群组的ID
     *
     * @param appId 应用ID
     * @param memberId 用户ID
     * @return ID集合
     */
    @Select("select group_id from im_group_member where app_id = #{param1} AND member_id = #{param2} ")
    List<String> getJoinedGroupId(Integer appId, String memberId);

    /**
     * 获取群组内的所有群成员ID
     *
     * @param appId 应用ID
     * @return ID集合
     */
    @Select("select member_id from im_group_member where app_id = #{param1} AND group_id = #{param2} and role != 3")
    List<String> getGroupMemberId(Integer appId, String groupId);

    /**
     * 获取群组管理员
     *
     * @param appId 应用ID
     * @param groupId 群组ID
     * @return {@link List}<{@link GroupMemberDTO}>
     */
    @Select("select member_id, `role` from im_group_member where app_id = #{param1} AND group_id = #{param2} and `role` in (1, 2)")
    List<GroupMemberDTO> getGroupManager(Integer appId, String groupId);

    /**
     * 查询用户加入的群组的ID
     *
     * @param appId 应用ID
     * @param operator 用户ID
     * @param code 状态码
     * @return ID集合
     */
    @Select("select group_id from im_group_member where app_id = #{param1} AND member_id = #{param2} and role != #{param3}" )
    List<String> syncMemberJoinedGroup(Integer appId, String operator, int code);
}