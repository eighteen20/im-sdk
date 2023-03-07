package cn.ctrlcv.im.serve.group.model.resp;

import lombok.Data;

/**
 * Class Name: AddMemberResp
 * Class Description: 邀请入群接口的响应对象
 *
 * @author liujm
 * @date 2023-03-07
 */
@Data
public class AddMemberResp {

    private String memberId;

    /**
     * 加人结果：0 为成功；1 为失败；2 为已经是群成员
     */
    private Integer result;

    private String resultMessage;

}
