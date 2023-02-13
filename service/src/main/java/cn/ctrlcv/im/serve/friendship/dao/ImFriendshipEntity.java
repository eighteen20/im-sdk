package cn.ctrlcv.im.serve.friendship.dao;

import java.io.Serializable;

import lombok.Data;

/**
 * 好友关系链表
 *
 * @author ljm19
 */
@Data
public class ImFriendshipEntity implements Serializable {
    /**
     * 应用ID
     */
    private Integer appId;

    /**
     * 好友添加发起者ID
     */
    private String fromId;

    /**
     * 好友接收者ID
     */
    private String toId;

    /**
     * 备注
     */
    private String remake;

    /**
     * 0-未添加  1-正常  2-删除
     */
    private Integer status;

    /**
     * 1-正常  2-拉黑
     */
    private Integer black;

    private String blackSequence;

    /**
     * 添加时间
     */
    private Long createTime;

    /**
     * seq
     */
    private Long friendSequence;

    /**
     * 添加来源
     */
    private String addSource;

    /**
     * 扩展字段
     */
    private String extra;

    private static final long serialVersionUID = 1L;
}