package cn.ctrlcv.im.serve.friendship.model.request;

import cn.ctrlcv.im.common.enums.FriendShipStatusEnum;
import cn.ctrlcv.im.common.model.RequestBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Class Name: ImportFriendshipReq
 * Class Description: 导入好友关系链数据请求参数
 *
 * @author liujm
 * @date 2023-02-08
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ImportFriendshipReq extends RequestBase {

    @NotNull(message = "fromId不能为空")
    public String fromId;

    public List<ImportFriendDTO> friendItem;


    @Data
    public static class ImportFriendDTO {

        private String toId;

        private String remake;

        private String addSource;

        private Integer status = FriendShipStatusEnum.FRIEND_STATUS_NO_FRIEND.getCode();;

        private Integer black = FriendShipStatusEnum.BLACK_STATUS_NORMAL.getCode();

    }
}
