package cn.ctrlcv.im.serve.conversation.controller;

import cn.ctrlcv.im.common.ResponseVO;
import cn.ctrlcv.im.serve.conversation.model.request.DeleteConversationReq;
import cn.ctrlcv.im.serve.conversation.model.request.UpdateConversationReq;
import cn.ctrlcv.im.serve.conversation.service.ConversationService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * Class Name: ConversationController
 * Class Description: 会话控制器
 *
 * @author liujm
 * @date 2023-04-04
 */
@RestController
@RequestMapping("v1/conversation")
public class ConversationController {

    @Resource
    private ConversationService conversationService;

    /**
     * 删除会话
     * @param req {@link DeleteConversationReq}
     * @param appId 应用id
     * @param identifier 操作者id
     * @return {@link ResponseVO}
     */
    @DeleteMapping("/deleteConversation")
    public ResponseVO deleteConversation(@RequestBody @Validated DeleteConversationReq req, Integer appId, String identifier)  {
        req.setAppId(appId);
        req.setOperator(identifier);
        return conversationService.deleteConversation(req);
    }

    /**
     * 更新会话: 置顶 or 免打扰
     * @param req {@link UpdateConversationReq}
     * @param appId 应用id
     * @param identifier 操作者id
     * @return {@link ResponseVO}
     */
    @PutMapping("/updateConversation")
    public ResponseVO updateConversation(@RequestBody @Validated UpdateConversationReq req, Integer appId, String identifier)  {
        req.setAppId(appId);
        req.setOperator(identifier);
        return conversationService.updateConversation(req);
    }

}
