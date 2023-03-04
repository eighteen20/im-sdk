package cn.ctrlcv.im.serve.group.dao.mapper;

import cn.ctrlcv.im.serve.group.dao.ImGroupMemberEntity;
import cn.ctrlcv.im.serve.group.model.dto.GroupMemberDTO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.lettuce.core.dynamic.annotation.Param;

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
}