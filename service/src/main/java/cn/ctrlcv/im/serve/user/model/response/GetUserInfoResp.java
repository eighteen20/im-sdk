package cn.ctrlcv.im.serve.user.model.response;

import cn.ctrlcv.im.serve.user.dao.ImUserDataEntity;
import lombok.Data;

import java.util.List;

/**
 * Class Name: GetUserInfoResp
 * Class Description: 获取用户信息的响应对象
 *
 * @author liujm
 * @date 2023-02-08
 */
@Data
public class GetUserInfoResp {

    private List<ImUserDataEntity> userDataItem;

    private List<String> failUser;

}
