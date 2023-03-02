package cn.ctrlcv.im.serve.group.controller;

import cn.ctrlcv.im.common.ResponseVO;
import cn.ctrlcv.im.serve.group.model.request.ImportGroupReq;
import cn.ctrlcv.im.serve.group.service.IGroupService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Class Name: GroupController
 * Class Description: 群组功能控制器
 *
 * @author liujm
 * @date 2023-03-02
 */
@RestController
@RequestMapping("v1/group")
public class GroupController {

    private final IGroupService groupService;

    public GroupController(IGroupService groupService) {
        this.groupService = groupService;
    }

    /**
     * 导入群组
     *
     * @param req {@link ImportGroupReq}
     * @param appId 应用ID
     * @param identifier 操作人
     * @return 无
     */
    @PostMapping("/importGroup")
    public ResponseVO<?> importGroup(@RequestBody @Validated ImportGroupReq req, Integer appId, String identifier)  {
        req.setAppId(appId);
        req.setOperator(identifier);
        return groupService.importGroup(req);
    }


}
