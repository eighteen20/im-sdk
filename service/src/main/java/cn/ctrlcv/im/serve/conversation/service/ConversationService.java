package cn.ctrlcv.im.serve.conversation.service;

import cn.ctrlcv.im.common.enums.ConversationTypeEnum;
import cn.ctrlcv.im.common.model.message.MessageReadedContent;
import cn.ctrlcv.im.serve.conversation.dao.ImConversationSetEntity;
import cn.ctrlcv.im.serve.conversation.dao.mapper.ImConversationSetMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Class Name: ConversationService
 * Class Description: 会话业务
 *
 * @author liujm
 * @date 2023-04-02
 */
@Service
public class ConversationService {

    @Resource
    private ImConversationSetMapper conversationSetMapper;


    /**
     * 标记会话，把已读的seq标记到会话
     *
     * @param messageReadedContent 消息已读内容
     */
    public void markConversation(MessageReadedContent messageReadedContent) {
        String toId = messageReadedContent.getToId();
        if (messageReadedContent.getConversationType() == ConversationTypeEnum.GROUP.getCode()) {
            toId = messageReadedContent.getGroupId();
        }
        // 生成会话ID
        String conversationId = generateConversationId(messageReadedContent.getConversationType(),
                messageReadedContent.getFromId(), toId);
        QueryWrapper<ImConversationSetEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ImConversationSetEntity.COL_CONVERSATION_ID, conversationId);
        queryWrapper.eq(ImConversationSetEntity.COL_APP_ID, messageReadedContent.getAppId());
        ImConversationSetEntity conversationSetEntity = conversationSetMapper.selectOne(queryWrapper);
        if (conversationSetEntity == null) {
            conversationSetEntity = new ImConversationSetEntity();
            BeanUtils.copyProperties(messageReadedContent, conversationSetEntity);
            conversationSetEntity.setConversationId(conversationId);
            conversationSetEntity.setReadSequence(messageReadedContent.getMessageSequence());
            conversationSetMapper.insert(conversationSetEntity);
        } else if (conversationSetEntity.getReadSequence() >= messageReadedContent.getMessageSequence()) {
            conversationSetEntity.setReadSequence(messageReadedContent.getMessageSequence());
            // 更新会话的Seq
            conversationSetMapper.readMark(conversationSetEntity);
        }

    }

    /**
     * 生成会话ID
     *
     * @param type 会话类型
     * @param fromId 发送者ID
     * @param toId   接收者ID
     *
     * @return 会话ID
     */
    public String generateConversationId(Integer type, String fromId, String toId) {
        return type + "_" + fromId + "_" + toId;
    }
}
