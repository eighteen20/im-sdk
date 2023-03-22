package cn.ctrlcv.im.serve.message.service;

import cn.ctrlcv.im.common.enums.DelFlagEnum;
import cn.ctrlcv.im.common.model.message.MessageContent;
import cn.ctrlcv.im.serve.message.dao.ImMessageBodyEntity;
import cn.ctrlcv.im.serve.message.dao.ImMessageHistoryEntity;
import cn.ctrlcv.im.serve.message.dao.mapper.ImMessageBodyMapper;
import cn.ctrlcv.im.serve.message.dao.mapper.ImMessageHistoryMapper;
import cn.ctrlcv.im.serve.utils.SnowflakeIdWorker;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Class Name: MessageStoreService
 * Class Description: 消息持久化服务
 *
 * @author liujm
 * @date 2023-03-22
 */
@Service
public class MessageStoreService {

    @Resource
    private ImMessageBodyMapper imMessageBodyMapper;

    @Resource
    private ImMessageHistoryMapper imMessageHistoryMapper;


    /**
     * 持久化私聊消息.
     * 1.messageContent转化为ImMessageBodyEntity
     * 2.插入ImMessageBodyEntity
     * 3.messageContent转化为ImMessageHistoryEntity
     * 4.批量插入ImMessageHistoryEntity
     *
     * @param messageContent 消息内容
     */
    public void storeP2pMessage(MessageContent messageContent) {
        // 1.messageContent转化为ImMessageBodyEntity
        ImMessageBodyEntity imMessageBodyEntity = this.convertToImMessageBodyEntity(messageContent);
        // 2.插入ImMessageBodyEntity
        this.imMessageBodyMapper.insert(imMessageBodyEntity);
        // 3.messageContent转化为ImMessageHistoryEntity
        List<ImMessageHistoryEntity> imMessageHistoryEntityList = this.convertToImMessageHistoryEntityList(messageContent, imMessageBodyEntity.getMessageKey());
        // 4.批量插入ImMessageHistoryEntity
        this.imMessageHistoryMapper.insertBatchSomeColumn(imMessageHistoryEntityList);

        messageContent.setMessageKey(imMessageBodyEntity.getMessageKey());
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

        ImMessageHistoryEntity toHistory = new ImMessageHistoryEntity();
        BeanUtils.copyProperties(messageContent, toHistory);
        toHistory.setOwnerId(messageContent.getToId());
        toHistory.setMessageKey(messageKey);
        toHistory.setCreateTime(System.currentTimeMillis());

        list.add(fromHistory);
        list.add(toHistory);
        return list;
    }

    /**
     * 消息内容转化为ImMessageBodyEntity
     *
     * @param messageContent 消息内容
     * @return ImMessageBodyEntity
     */
    private ImMessageBodyEntity convertToImMessageBodyEntity(MessageContent messageContent) {
        ImMessageBodyEntity messageBody = new ImMessageBodyEntity();
        messageBody.setMessageKey(SnowflakeIdWorker.nextId());
        messageBody.setAppId(messageContent.getAppId());
        messageBody.setCreateTime(System.currentTimeMillis());
        messageBody.setSecurityKey("");
        messageBody.setExtra(messageContent.getExtra());
        messageBody.setDelFlag(DelFlagEnum.NORMAL.getCode());
        messageBody.setMessageTime(messageContent.getMessageTime());
        messageBody.setMessageBody(messageContent.getMessageBody());
        return messageBody;
    }
}
