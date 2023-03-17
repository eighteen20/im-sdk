package cn.ctrlcv.im.serve.group.model.callback;

import lombok.Data;

/**
 * Class Name: DestroyGroupCallbackDTO
 * Class Description: 解散群后的回调参数
 *
 * @author liujm
 * @date 2023-03-17
 */
@Data
public class DestroyGroupAfterCallbackDTO {

    private String groupId;
}
