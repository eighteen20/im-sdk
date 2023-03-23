package cn.ctrlcv.im.messagestore.service;

import cn.ctrlcv.im.common.exception.ApplicationException;
import cn.ctrlcv.im.common.model.message.MessageContent;
import cn.ctrlcv.im.messagestore.dao.ImMessageHistoryEntity;
import cn.ctrlcv.im.messagestore.dao.mapper.ImMessageBodyMapper;
import cn.ctrlcv.im.messagestore.dao.mapper.ImMessageHistoryMapper;
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

        ImMessageHistoryEntity toHistory = new ImMessageHistoryEntity();
        BeanUtils.copyProperties(messageContent, toHistory);
        toHistory.setOwnerId(messageContent.getToId());
        toHistory.setMessageKey(messageKey);
        toHistory.setCreateTime(System.currentTimeMillis());

        list.add(fromHistory);
        list.add(toHistory);
        return list;
    }

}
