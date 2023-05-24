package cn.ctrlcv.im.codec.pack.group;

import lombok.Data;

/**
 * Class Name: DestroyGroupPack
 * Class Description: 解散群通知报文
 *
 * @author liujm
 * @date 2023-03-21
 */
@Data
public class DestroyGroupPack {

    private String groupId;

    private Long sequence;

}
