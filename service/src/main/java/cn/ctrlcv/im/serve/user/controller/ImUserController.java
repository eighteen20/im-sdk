package cn.ctrlcv.im.serve.user.controller;

import cn.ctrlcv.im.common.ResponseVO;
import cn.ctrlcv.im.serve.user.model.request.ImportUserReq;
import cn.ctrlcv.im.serve.user.service.IUserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    public ImUserController(IUserService userService) {
        this.userService = userService;
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


}
