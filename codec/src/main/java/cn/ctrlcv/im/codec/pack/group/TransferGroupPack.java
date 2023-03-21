package cn.ctrlcv.im.codec.pack.group;

import lombok.Data;

/**
 * Class Name: TransferGroupPack
 * Class Description: 转让群主通知报文
 *
 * @author liujm
 * @date 2023-03-21
 */
@Data
public class TransferGroupPack {

        private String groupId;

        private String ownerId;
}
