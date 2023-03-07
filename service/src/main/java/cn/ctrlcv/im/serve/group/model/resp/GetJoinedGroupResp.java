package cn.ctrlcv.im.serve.group.model.resp;

import cn.ctrlcv.im.serve.group.dao.ImGroupEntity;
import lombok.Data;

import java.util.List;

/**
 * Class Name: GetJoinedGroupResp
 * Class Description: 获取用户加入的群列表接口的响应对象
 *
 * @author liujm
 * @date 2023-03-07
 */
@Data
public class GetJoinedGroupResp {

    private Integer totalCount;

    private List<ImGroupEntity> groupList;
}
