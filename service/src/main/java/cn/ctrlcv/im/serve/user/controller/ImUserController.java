package cn.ctrlcv.im.serve.user.controller;

import cn.ctrlcv.im.common.ClientTypeEnum;
import cn.ctrlcv.im.common.ResponseVO;
import cn.ctrlcv.im.common.route.RouteHandler;
import cn.ctrlcv.im.common.route.RouteInfo;
import cn.ctrlcv.im.common.utils.RouteInfoParseUtil;
import cn.ctrlcv.im.serve.user.model.request.*;
import cn.ctrlcv.im.serve.user.service.IUserService;
import cn.ctrlcv.im.serve.user.service.IUserStatusService;
import cn.ctrlcv.im.serve.utils.ZkUtil;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Class Name: ImUserController
 * Class Description: 系统用户控制层
 *
 * @author liujm
 * @date 2023-02-07
 */
@RestController
@RequestMapping("/v1/user")
public class ImUserController {

    private final IUserService userService;
    private final RouteHandler routeHandler;
    private final ZkUtil zkUtil;
    @Resource
    private IUserStatusService userStatusService;


    public ImUserController(IUserService userService, RouteHandler routeHandler, ZkUtil zkUtil) {
        this.userService = userService;
        this.routeHandler = routeHandler;
        this.zkUtil = zkUtil;
    }

    /**
     * 导入用户资料
     *
     * @param req {@link ImportUserReq}
     * @param appId 应用ID
     * @return {@link ResponseVO}
     */
    @PostMapping("/importUser")
    public ResponseVO<?> importUser(@RequestBody ImportUserReq req, Integer appId) {
        req.setAppId(appId);
        return this.userService.importUser(req);
    }


    /**
     * im的登录接口，返回im地址
     *
     * @param req {@link LoginReq}
     * @param appId 应用Id
     * @return {@link RouteInfo}
     */
    @PostMapping("/login")
    public ResponseVO<?> login(@RequestBody @Validated LoginReq req, Integer appId) {
        req.setAppId(appId);

        ResponseVO<?> login = this.userService.login(req);
        if (login.isOk()) {
            // 去zookeeper 获取一个IM的地址， 返回出去
            List<String> allNode;
            if (req.getClientType() == ClientTypeEnum.WEB.getCode()) {
                allNode = this.zkUtil.getAllWsNode();
            } else {
                allNode = this.zkUtil.getAllTcpNode();
            }
            String s = this.routeHandler.routerServer(allNode, req.getUserId());
            RouteInfo parse = RouteInfoParseUtil.parseBean(s);
            return ResponseVO.successResponse(parse);
        }

        return ResponseVO.errorResponse();
    }


    /**
     * 获取用户的sequence
     *
     * @param req {@link GetUserSequenceReq}
     * @param appId 应用ID
     * @return
     */
    @GetMapping("/getSequence")
    public ResponseVO<Map<Object, Object>> getSequence(@RequestBody @Validated GetUserSequenceReq req, Integer appId) {
        req.setAppId(appId);
        return this.userService.getSequence(req);
    }

    /**
     * 订阅用户在线状态
     *
     * @param req {@link SubscribeUserOnlineStatusReq}
     * @param appId 应用ID
     * @param identifier 用户ID
     *
     * @return {@link ResponseVO}
     */
    @PostMapping("/subscribeUserOnlineStatus")
    public ResponseVO<?> subscribeUserOnlineStatus(@RequestBody @Validated SubscribeUserOnlineStatusReq req, Integer appId, String identifier) {
        req.setAppId(appId);
        req.setOperator(identifier);
        return this.userStatusService.subscribeUserOnlineStatus(req);
    }

    /**
     * 用户设置自定义状态
     *
     * @param req {@link UserSetCustomStatusReq}
     * @param appId 应用ID
     *              @param identifier 用户ID
     * @return {@link ResponseVO}
     */
    @PostMapping("/setCustomStatus")
    public ResponseVO<?> setCustomStatus(@RequestBody @Validated UserSetCustomStatusReq req, Integer appId, String identifier) {
        req.setAppId(appId);
        req.setOperator(identifier);
        return this.userStatusService.setCustomStatus(req);
    }


}
