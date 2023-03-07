package cn.ctrlcv.im.serve.group.model.request;

import cn.ctrlcv.im.common.model.RequestBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Class Name: SpeakMemberReq
 * Class Description: 禁言群成员接口的请求参数
 *
 * @author liujm
 * @date 2023-03-07
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SpeakMemberReq extends RequestBase {

    @NotBlank(message = "群id不能为空")
    private String groupId;

    @NotBlank(message = "memberId不能为空")
    private String memberId;

    /**
     * 禁言时间，单位毫秒
     */
    @NotNull(message = "禁言时间不能为空")
    private Long speakDate;


}
