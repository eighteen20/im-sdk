package cn.ctrlcv.im.serve.conversation.dao.mapper;

import cn.ctrlcv.im.serve.conversation.dao.ImConversationSetEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Update;

/**
 * @author ljm19
 */
public interface ImConversationSetMapper extends BaseMapper<ImConversationSetEntity> {

    /**
     * 会话已读标记
     * @param conversationSetEntity 会话实体
     */
    @Update(" update im_conversation_set set read_sequence = #{readSequence} " +
            " where conversation_id = #{conversationId} and app_id = #{appId} AND read_sequence < #{readSequence}")
    void readMark(ImConversationSetEntity conversationSetEntity);
}