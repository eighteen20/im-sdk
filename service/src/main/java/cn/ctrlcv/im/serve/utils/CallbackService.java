package cn.ctrlcv.im.serve.utils;

import cn.ctrlcv.im.common.model.ResponseVO;
import cn.ctrlcv.im.common.config.ImConfig;
import cn.ctrlcv.im.common.utils.HttpRequestUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * Class Name: CallbackService
 * Class Description: 回调服务
 *
 * @author liujm
 * @date 2023-03-16
 */
@Slf4j
@Component
public class CallbackService {

    @Resource
    HttpRequestUtils httpRequestUtils;

    @Resource
    ImConfig imConfig;

    @Resource
    ShareThreadPool shareThreadPool;


    /**
     * 置后回调
     *
     * @param appId 应用ID
     * @param callbackCommand 回调内容指令
     * @param jsonBody 回调内容
     */
    public void callback(Integer appId, String callbackCommand, String jsonBody) {
        this.shareThreadPool.submit(() -> {
            try {
                this.httpRequestUtils.doPost(this.imConfig.getCallbackUrl(), Object.class, builderUrlParams(appId, callbackCommand),
                        jsonBody, null);
            } catch (Exception e) {
                log.error("callback 回调{} : {}出现异常 ： {} ", callbackCommand, appId, e.getMessage());
            }
        });
    }

    /**
     * 置前回调
     *
     * @param appId 应用ID
     * @param callbackCommand 回调内容指令
     * @param jsonBody 回调内容
     * @return
     */
    public ResponseVO beforeCallback(Integer appId, String callbackCommand, String jsonBody) {
        try {
            ResponseVO responseVO = httpRequestUtils.doPost("", ResponseVO.class, builderUrlParams(appId, callbackCommand),
                    jsonBody, null);
            return responseVO;
        } catch (Exception e) {
            log.error("callback 之前 回调{} : {}出现异常 ： {} ", callbackCommand, appId, e.getMessage());
            return ResponseVO.successResponse();
        }
    }

    private Map<String, Object> builderUrlParams(Integer appId, String command) {
        Map<String, Object> map = new HashMap<>(2);
        map.put("appId", appId);
        map.put("command", command);
        return map;
    }


}
