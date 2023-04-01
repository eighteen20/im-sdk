package cn.ctrlcv.im.messagestore.service;

import cn.ctrlcv.im.common.exception.ApplicationException;
import cn.ctrlcv.im.common.model.message.GroupChatMessageContent;
import cn.ctrlcv.im.common.model.message.MessageContent;
import cn.ctrlcv.im.messagestore.dao.ImGroupMessageHistoryEntity;
import cn.ctrlcv.im.messagestore.dao.ImMessageHistoryEntity;
import cn.ctrlcv.im.messagestore.dao.mapper.ImGroupMessageHistoryMapper;
import cn.ctrlcv.im.messagestore.dao.mapper.ImMessageBodyMapper;
import cn.ctrlcv.im.messagestore.dao.mapper.ImMessageHistoryMapper;
import cn.ctrlcv.im.messagestore.model.DoStoreGroupMessageDTO;
import cn.ctrlcv.im.messagestore.model.DoStoreP2pMessageDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Class Name: StoreMessageService
 * Class Description: 持久化消息服务
 *
 * @author liujm
 * @date 2023-03-23
 */
@Service
public class StoreMessageService {

    @Resource
    ImMessageHistoryMapper imMessageHistoryMapper;

    @Resource
    ImMessageBodyMapper imMessageBodyMapper;

    @Resource
    ImGroupMessageHistoryMapper imGroupMessageHistoryMapper;

    @Transactional(rollbackFor = ApplicationException.class)
    public void doStoreP2pMessage(DoStoreP2pMessageDTO dto) {
        imMessageBodyMapper.insert(dto.getImMessageBodyEntity());
        List<ImMessageHistoryEntity> list = convertToImMessageHistoryEntityList(dto.getMessageContent(), dto.getImMessageBodyEntity().getMessageKey());
        imMessageHistoryMapper.insertBatchSomeColumn(list);
    }

    /**
     * 消息内容转化为ImMessageHistoryEntity
     *
     * @param messageContent 消息内容
     * @param messageKey     消息主键
     * @return ImMessageHistoryEntity
     */
    private List<ImMessageHistoryEntity> convertToImMessageHistoryEntityList(MessageContent messageContent, Long messageKey) {
        List<ImMessageHistoryEntity> list = new ArrayList<>();
        ImMessageHistoryEntity fromHistory = new ImMessageHistoryEntity();
        BeanUtils.copyProperties(messageContent, fromHistory);
        fromHistory.setOwnerId(messageContent.getFromId());
        fromHistory.setMessageKey(messageKey);
        fromHistory.setCreateTime(System.currentTimeMillis());
        fromHistory.setSequence(messageContent.getMessageSequence());

        ImMessageHistoryEntity toHistory = new ImMessageHistoryEntity();
        BeanUtils.copyProperties(messageContent, toHistory);
        toHistory.setOwnerId(messageContent.getToId());
        toHistory.setMessageKey(messageKey);
        toHistory.setCreateTime(System.currentTimeMillis());
        toHistory.setSequence(messageContent.getMessageSequence());

        list.add(fromHistory);
        list.add(toHistory);
        return list;
    }


    /**
     * 持久化群聊消息
     *
     * @param storeGroupMessageDTO 持久化群聊消息DTO
     */
    @Transactional(rollbackFor = ApplicationException.class)
    public void doStoreGroupMessage(DoStoreGroupMessageDTO storeGroupMessageDTO) {
        imMessageBodyMapper.insert(storeGroupMessageDTO.getImMessageBodyEntity());
        ImGroupMessageHistoryEntity historyEntity = convertToImGroupMessageHistoryEntityList(storeGroupMessageDTO.getGroupChatMessageContent(),
                storeGroupMessageDTO.getImMessageBodyEntity().getMessageKey());
        imGroupMessageHistoryMapper.insert(historyEntity);
    }

    /**
     * 消息内容转化为ImGroupMessageHistoryEntity
     *
     * @param messageContent 消息内容
     * @param messageKey     消息主键
     * @return ImGroupMessageHistoryEntity
     */
    private ImGroupMessageHistoryEntity convertToImGroupMessageHistoryEntityList(GroupChatMessageContent messageContent, Long messageKey) {
        ImGroupMessageHistoryEntity entity = new ImGroupMessageHistoryEntity();
        BeanUtils.copyProperties(messageContent, entity);
        entity.setMessageKey(messageKey);
        entity.setGroupId(messageContent.getGroupId());
        entity.setCreateTime(System.currentTimeMillis());
        return entity;
    }
}
