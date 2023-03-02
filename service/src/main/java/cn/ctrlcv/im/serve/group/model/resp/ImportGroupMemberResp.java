package cn.ctrlcv.im.serve.group.model.resp;

import lombok.Data;

/**
 * Class Name: ImportGroupMemberResp
 * Class Description: 导入群成员接口响应结果
 *
 * @author liujm
 * @date 2023-03-02
 */
@Data
public class ImportGroupMemberResp {

    /**
     * 成员ID
     */
    private String memberId;

    /**
     * 加人结果：0 为成功；1 为失败；2 为已经是群成员
     */
    private Integer result;

    private String resultMessage;

}
