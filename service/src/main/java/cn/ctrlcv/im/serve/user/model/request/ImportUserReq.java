package cn.ctrlcv.im.serve.user.model.request;

import cn.ctrlcv.im.common.model.RequestBase;
import cn.ctrlcv.im.serve.user.dao.ImUserDataEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * Class Name: ImportUserReq
 * Class Description: 导入用户资料请求对象
 *
 * @author liujm
 * @date 2023-02-06
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ImportUserReq extends RequestBase {

    private List<ImUserDataEntity> userList;


}
