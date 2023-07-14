package cn.ctrlcv.im.serve.message.controller;

import cn.ctrlcv.im.common.model.ResponseVO;
import cn.ctrlcv.im.common.model.SyncReq;
import cn.ctrlcv.im.common.model.SyncResp;
import cn.ctrlcv.im.common.model.message.CheckSendMessageReq;
import cn.ctrlcv.im.common.model.message.OfflineMessageContent;
import cn.ctrlcv.im.serve.message.model.request.GroupSendMessageReq;
import cn.ctrlcv.im.serve.message.model.request.P2pSendMessageReq;
import cn.ctrlcv.im.serve.message.service.GroupMessageService;
import cn.ctrlcv.im.serve.message.service.MessageSyncService;
import cn.ctrlcv.im.serve.message.service.P2pMessageService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * Class Name: MessageController
 * Class Description: TODO
 *
 * @author liujm
 * @date 2023-03-23
 */
@RestController
@RequestMapping("/v1/message")
public class MessageController {

    private final P2pMessageService p2pMessageService;
    private final GroupMessageService groupMessageService;

    @Resource
    private MessageSyncService messageSyncService;

    public MessageController(P2pMessageService p2pMessageService, GroupMessageService groupMessageService) {
        this.p2pMessageService = p2pMessageService;
        this.groupMessageService = groupMessageService;
    }

    /**
     * 发送单聊消息
     *
     * @param req   请求参数
     * @param appId 应用id
     * @return 响应参数
     */
    @PostMapping("/p2p/send")
    public ResponseVO send(@RequestBody @Validated P2pSendMessageReq req, Integer appId) {
        req.setAppId(appId);
        return ResponseVO.successResponse(p2pMessageService.send(req));
    }

    /**
     * 发送群聊消息
     *
     * @param req        请求参数
     * @param appId      应用id
     * @param identifier 操作者
     * @return 响应参数
     */
    @PostMapping("/group/message")
    public ResponseVO sendMessage(@RequestBody @Validated GroupSendMessageReq req, Integer appId, String identifier) {
        req.setAppId(appId);
        req.setOperator(identifier);
        return ResponseVO.successResponse(groupMessageService.send(req));
    }


    /**
     * 检查消息发送权限
     *
     * @param req 请求参数
     * @return 响应参数
     */
    @PostMapping("/p2p/checkSend")
    public ResponseVO checkSend(@RequestBody @Validated CheckSendMessageReq req) {
        return ResponseVO.successResponse(p2pMessageService.checkImServicePermission(req.getFromId(), req.getToId(), req.getAppId()));
    }

    /**
     * 同步用户的离线消息
     *
     * @param req        {@link SyncReq}
     * @param appId      应用id
     * @param identifier 用户ID
     * @return resp {@link SyncResp}
     */
    @PostMapping("/syncOfflineMessage")
    public ResponseVO<SyncResp<OfflineMessageContent>> syncOfflineMessage(@RequestBody @Validated SyncReq req, Integer appId, String identifier) {
        req.setAppId(appId);
        req.setOperator(identifier);
        return ResponseVO.successResponse(messageSyncService.syncOfflineMessage(req));
    }


}
