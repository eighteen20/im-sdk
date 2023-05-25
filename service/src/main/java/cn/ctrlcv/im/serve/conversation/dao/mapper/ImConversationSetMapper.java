package cn.ctrlcv.im.serve.conversation.dao.mapper;

import cn.ctrlcv.im.serve.conversation.dao.ImConversationSetEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * @author ljm19
 */
public interface ImConversationSetMapper extends BaseMapper<ImConversationSetEntity> {

    /**
     * 会话已读标记
     *
     * @param conversationSetEntity 会话实体
     */
    @Update(" update im_conversation_set set read_sequence = #{readSequence}, sequence = #{sequence} " +
            " where conversation_id = #{conversationId} and app_id = #{appId} AND read_sequence < #{readSequence}")
    void readMark(ImConversationSetEntity conversationSetEntity);

    /**
     * 查询最大的 sequence
     *
     * @param appId 应用ID
     * @param userId 用户ID
     * @return {@link Long}
     */
    @Select("select max(sequence) from im_conversation_set where app_id = #{param1} and from_id = #{param2}")
    Long getMaxFriendSequence(Integer appId, String userId);

}