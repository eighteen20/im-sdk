package cn.ctrlcv.im.serve.friendship.model.response;

import lombok.Data;

import java.util.List;

/**
 * Class Name: ImportFriendShipResp
 * Class Description: 导入好友关系链的响应对象
 *
 * @author liujm
 * @date 2023-02-09
 */
@Data
public class ImportFriendShipResp {

    private List<String> successId;

    private List<String> errorId;

}
