package cn.ctrlcv.im.serve.user.service;

import cn.ctrlcv.im.common.ResponseVO;
import cn.ctrlcv.im.serve.user.dao.ImUserDataEntity;
import cn.ctrlcv.im.serve.user.model.request.DeleteUserReq;
import cn.ctrlcv.im.serve.user.model.request.GetUserInfoReq;
import cn.ctrlcv.im.serve.user.model.request.ImportUserReq;
import cn.ctrlcv.im.serve.user.model.request.ModifyUserInfoReq;
import cn.ctrlcv.im.serve.user.model.response.GetUserInfoResp;
import cn.ctrlcv.im.serve.user.model.response.ImportUserResp;

/**
 * interface Name: IUserService
 * interface Description: 用户资料业务逻辑接口
 *
 * @author liujm
 * @date 2023-02-03
 */
public interface IUserService {

    /**
     * 导入用户
     *
     * @param req {@link ImportUserReq}
     * @return {@link ImportUserResp}
     */
    ResponseVO<ImportUserResp> importUser(ImportUserReq req);

    /**
     * 获取批量用户信息
     *
     * @param req {@link GetUserInfoReq}
     * @return {@link GetUserInfoResp}
     */
    ResponseVO<GetUserInfoResp> getUserInfo(GetUserInfoReq req);

    /**
     * 获取单个的用户信息
     *
     * @param userId 用户ID
     * @param appId 应用ID
     * @return {@link ImUserDataEntity}
     */
    ResponseVO<ImUserDataEntity> getSingleUserInfo(String userId, Integer appId);

    /**
     * 删除用户信息
     *
     * @param req {@link DeleteUserReq}
     * @return
     */
    ResponseVO<?> deleteUser(DeleteUserReq req);

    /**
     * ModifyUserInfoReq
     *
     * @param req {@link ModifyUserInfoReq}
     * @return
     */
    ResponseVO<?> modifyUserInfo(ModifyUserInfoReq req);
}
