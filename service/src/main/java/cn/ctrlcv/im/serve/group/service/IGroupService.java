package cn.ctrlcv.im.serve.group.service;

import cn.ctrlcv.im.common.ResponseVO;
import cn.ctrlcv.im.serve.group.dao.ImGroupEntity;
import cn.ctrlcv.im.serve.group.model.request.ImportGroupReq;

/**
 * interface Name: IGroupService
 * interface Description: 群组功能接口定义
 *
 *
 * @author liujm
 * @date 2023-03-02
 */
public interface IGroupService {

    /**
     * 导入群组
     *
     * @param req {@link ImportGroupReq}
     * @return
     */
    ResponseVO<?> importGroup(ImportGroupReq req);

    /**
     * 获取群组信息
     *
     * @param groupId 群ID
     * @param appId 应用ID
     * @return {@link ImGroupEntity}
     */
    ResponseVO<ImGroupEntity> getGroup(String groupId, Integer appId);

}
