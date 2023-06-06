package cn.ctrlcv.im.serve.conversation.service;

import cn.ctrlcv.im.codec.pack.conversation.DeleteConversationPack;
import cn.ctrlcv.im.codec.pack.conversation.UpdateConversationPack;
import cn.ctrlcv.im.common.ResponseVO;
import cn.ctrlcv.im.common.config.ImConfig;
import cn.ctrlcv.im.common.constant.Constants;
import cn.ctrlcv.im.common.enums.ConversationErrorCodeEnum;
import cn.ctrlcv.im.common.enums.ConversationTypeEnum;
import cn.ctrlcv.im.common.enums.command.ConversationEventCommand;
import cn.ctrlcv.im.common.model.ClientInfo;
import cn.ctrlcv.im.common.model.SyncReq;
import cn.ctrlcv.im.common.model.SyncResp;
import cn.ctrlcv.im.common.model.message.MessageReadedContent;
import cn.ctrlcv.im.serve.conversation.dao.ImConversationSetEntity;
import cn.ctrlcv.im.serve.conversation.dao.mapper.ImConversationSetMapper;
import cn.ctrlcv.im.serve.conversation.model.request.DeleteConversationReq;
import cn.ctrlcv.im.serve.conversation.model.request.UpdateConversationReq;
import cn.ctrlcv.im.serve.friendship.dao.ImFriendshipEntity;
import cn.ctrlcv.im.serve.sequence.RedisSeq;
import cn.ctrlcv.im.serve.utils.MessageProducer;
import cn.ctrlcv.im.serve.utils.WriteUserSeq;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

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

    @Resource
    private MessageProducer messageProducer;

    @Resource
    private ImConfig imConfig;

    @Resource
    private RedisSeq redisSeq;

    @Resource
    private WriteUserSeq writeUserSeq;


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
            long seq = redisSeq.nextSeq(messageReadedContent.getAppId() + Constants.SeqConstants.CONVERSATION);
            BeanUtils.copyProperties(messageReadedContent, conversationSetEntity);
            conversationSetEntity.setConversationId(conversationId);
            conversationSetEntity.setToId(toId);
            conversationSetEntity.setReadSequence(messageReadedContent.getMessageSequence());
            conversationSetEntity.setReadSequence(seq);
            conversationSetMapper.insert(conversationSetEntity);
            writeUserSeq.writeUserSeq(messageReadedContent.getAppId(), messageReadedContent.getFromId(),
                    Constants.SeqConstants.CONVERSATION, seq);
        } else if (conversationSetEntity.getReadSequence() >= messageReadedContent.getMessageSequence()) {
            long seq = redisSeq.nextSeq(messageReadedContent.getAppId() + Constants.SeqConstants.CONVERSATION);
            conversationSetEntity.setSequence(seq);
            conversationSetEntity.setReadSequence(messageReadedContent.getMessageSequence());
            // 更新会话的Seq
            conversationSetMapper.readMark(conversationSetEntity);
            writeUserSeq.writeUserSeq(messageReadedContent.getAppId(), messageReadedContent.getFromId(),
                    Constants.SeqConstants.CONVERSATION, seq);
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

    /**
     * 删除会话
     *
     * @param req 删除会话请求
     *
     * @return 删除会话响应
     */
    public ResponseVO deleteConversation(DeleteConversationReq req) {

        /*QueryWrapper<ImConversationSetEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ImConversationSetEntity.COL_CONVERSATION_ID, req.getConversationId());
        queryWrapper.eq(ImConversationSetEntity.COL_APP_ID, req.getAppId());
        ImConversationSetEntity imConversationSetEntity = conversationSetMapper.selectOne(queryWrapper);
        if (imConversationSetEntity != null) {
            imConversationSetEntity.setIsMute(0);
            imConversationSetEntity.setIsTop(0);
            conversationSetMapper.update(imConversationSetEntity, queryWrapper);
        }*/

        if (imConfig.getDeleteConversationSyncMode() == 1) {
            DeleteConversationPack pack = new DeleteConversationPack();
            pack.setConversationId(req.getConversationId());
            messageProducer.sendToUser(req.getFromId(), ConversationEventCommand.CONVERSATION_DELETE, pack, new ClientInfo(req.getAppId(), req.getClientType(), req.getImei()));
        }
        return ResponseVO.successResponse();
    }

    /**
     * 更新会话
     *
     * @param req 更新会话请求
     *
     * @return 更新会话响应
     */
    public ResponseVO updateConversation(UpdateConversationReq req) {
        if(req.getIsTop() == null && req.getIsMute() == null){
            return ResponseVO.errorResponse(ConversationErrorCodeEnum.CONVERSATION_UPDATE_PARAM_ERROR);
        }
        QueryWrapper<ImConversationSetEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ImConversationSetEntity.COL_CONVERSATION_ID,req.getConversationId());
        queryWrapper.eq(ImConversationSetEntity.COL_APP_ID,req.getAppId());
        ImConversationSetEntity imConversationSetEntity = conversationSetMapper.selectOne(queryWrapper);
        if(imConversationSetEntity != null){
            long seq = redisSeq.nextSeq(req.getAppId() + Constants.SeqConstants.CONVERSATION);

            if(req.getIsMute() != null){
                imConversationSetEntity.setIsTop(req.getIsTop());
            }
            if(req.getIsMute() != null){
                imConversationSetEntity.setIsMute(req.getIsMute());
            }
            imConversationSetEntity.setSequence(seq);
            conversationSetMapper.update(imConversationSetEntity,queryWrapper);
            writeUserSeq.writeUserSeq(req.getAppId(), req.getFromId(), Constants.SeqConstants.CONVERSATION, seq);

            UpdateConversationPack pack = new UpdateConversationPack();
            pack.setConversationId(req.getConversationId());
            pack.setIsMute(imConversationSetEntity.getIsMute());
            pack.setIsTop(imConversationSetEntity.getIsTop());
            pack.setSequence(seq);
            pack.setConversationType(imConversationSetEntity.getConversationType());
            messageProducer.sendToUserExceptClient(req.getFromId(), ConversationEventCommand.CONVERSATION_UPDATE, pack,
                    new ClientInfo(req.getAppId(),req.getClientType(), req.getImei()));
        }
        return ResponseVO.successResponse();
    }

    /**
     * 同步会话列表
     *
     * @param req 同步会话列表请求
     * @return 同步会话列表响应
     */
    public ResponseVO<SyncResp<ImConversationSetEntity>> syncConversationList(SyncReq req) {
        if (req.getMaxLimit() > 100) {
            req.setMaxLimit(100);
        }
        SyncResp<ImConversationSetEntity> syncResp = new SyncResp<>();
        QueryWrapper<ImConversationSetEntity> query = new QueryWrapper<>();
        query.lambda()
                .eq(ImConversationSetEntity::getAppId, req.getAppId())
                .eq(ImConversationSetEntity::getFromId, req.getOperator())
                .gt(ImConversationSetEntity::getSequence, req.getLastSequence())
                .last("limit " + req.getMaxLimit())
                .orderByAsc(ImConversationSetEntity::getSequence);

        List<ImConversationSetEntity> entityList = conversationSetMapper.selectList(query);

        if (CollectionUtils.isEmpty(entityList)) {
            ImConversationSetEntity lastEntity = entityList.get(entityList.size() - 1);
            Long maxConversationSetSequence = conversationSetMapper.getMaxFriendSequence(req.getAppId(), req.getOperator());
            syncResp.setMaxSequence(maxConversationSetSequence);
            syncResp.setCompleted(lastEntity.getSequence() >= maxConversationSetSequence);
            syncResp.setDataList(entityList);
            return ResponseVO.successResponse(syncResp);
        }

        syncResp.setCompleted(true);
        return ResponseVO.successResponse(syncResp);
    }

    /**
     *  生成会话ID
     *
     * @param conversationType
     * @param fromId
     * @param toId
     * @return
     */
    public String convertConversationId(Integer conversationType, String fromId, String toId) {
        return conversationType + "_" + fromId + "_" + toId;
    }
}
