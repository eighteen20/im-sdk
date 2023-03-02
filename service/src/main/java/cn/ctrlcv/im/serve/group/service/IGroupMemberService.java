package cn.ctrlcv.im.serve.group.service;

import cn.ctrlcv.im.common.ResponseVO;
import cn.ctrlcv.im.serve.group.model.dto.GroupMemberDTO;
import cn.ctrlcv.im.serve.group.model.request.ImportGroupMemberReq;

/**
 * interface Name: IGroupMemberService
 * interface Description: 群成员操作接口
 *
 * @author liujm
 * @date 2023-03-02
 */
public interface IGroupMemberService {

    /**
     * 导入群成员
     *
     * @param req {@link ImportGroupMemberReq}
     * @return
     */
    ResponseVO<?> importGroupMember(ImportGroupMemberReq req);

    /**
     * 插入单条群成员记录
     *
     * @param groupId 群组ID
     * @param appId 应用ID
     * @param dto 群成员信息
     * @return
     */
    ResponseVO<?> addGroupMember(String groupId, Integer appId, GroupMemberDTO dto);

}
