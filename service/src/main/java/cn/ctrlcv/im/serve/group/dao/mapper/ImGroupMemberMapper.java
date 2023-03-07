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
}