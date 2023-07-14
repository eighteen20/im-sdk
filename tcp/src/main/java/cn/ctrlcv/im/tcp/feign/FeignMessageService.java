package cn.ctrlcv.im.tcp.feign;

import cn.ctrlcv.im.common.model.ResponseVO;
import cn.ctrlcv.im.common.model.message.CheckSendMessageReq;
import feign.Headers;
import feign.RequestLine;

/**
 * interface Name: FeignMessageService
 * interface Description: 消息服务feign接口
 *
 * @author liujm
 * @date 2023-03-23
 */
public interface FeignMessageService {

    /**
     * 消息发送前的检查
     *
     * @param req 检查消息发送请求参数
     * @return 检查消息发送响应参数
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @RequestLine("POST message/p2p/checkSend")
    ResponseVO checkSendMessage(CheckSendMessageReq req);
}
