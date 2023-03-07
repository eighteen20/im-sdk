package cn.ctrlcv.im.serve.group.model.request;

import cn.ctrlcv.im.common.model.RequestBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * Class Name: GetJoinedGroupReq
 * Class Description: 获取用户加入的群列表接口的请求参数
 *
 * @author liujm
 * @date 2023-03-07
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GetJoinedGroupReq extends RequestBase {

    @NotBlank(message = "用户id不能为空")
    private String memberId;

    /**
     * 群类型
     */
    private List<Integer> groupType;

    /**
     * 单次拉取的群组数量，如果不填代表所有群组
     */
    private Integer limit;

    /**
     * 第几页
     */
    private Integer offset;
}
