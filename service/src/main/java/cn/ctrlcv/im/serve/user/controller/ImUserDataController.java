package cn.ctrlcv.im.serve.user.controller;

import cn.ctrlcv.im.common.ResponseVO;
import cn.ctrlcv.im.serve.user.dao.ImUserDataEntity;
import cn.ctrlcv.im.serve.user.model.request.GetUserInfoReq;
import cn.ctrlcv.im.serve.user.model.request.ModifyUserInfoReq;
import cn.ctrlcv.im.serve.user.model.request.UserId;
import cn.ctrlcv.im.serve.user.model.response.GetUserInfoResp;
import cn.ctrlcv.im.serve.user.service.IUserService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Class Name: ImUserDataController
 * Class Description: 用户数据控制层
 *
 * @author liujm
 * @date 2023-02-07
 */
public class ImUserDataController {

    private final IUserService userService;

    public ImUserDataController(IUserService userService) {
        this.userService = userService;
    }

    @GetMapping("/getUserInfo")
    public ResponseVO<GetUserInfoResp> getUserInfo(@RequestBody GetUserInfoReq req, Integer appId) {
        req.setAppId(appId);
        return userService.getUserInfo(req);
    }

    @GetMapping("/getSingleUserInfo")
    public ResponseVO<ImUserDataEntity> getSingleUserInfo(@RequestBody @Validated UserId req, Integer appId) {
        req.setAppId(appId);
        return userService.getSingleUserInfo(req.getUserId(), req.getAppId());
    }

    @PostMapping("/modifyUserInfo")
    public ResponseVO<?> modifyUserInfo(@RequestBody @Validated ModifyUserInfoReq req, Integer appId) {
        req.setAppId(appId);
        return userService.modifyUserInfo(req);
    }

}
