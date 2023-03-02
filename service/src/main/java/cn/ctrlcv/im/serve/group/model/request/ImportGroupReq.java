package cn.ctrlcv.im.serve.group.model.request;

import cn.ctrlcv.im.common.model.RequestBase;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

/**
 * Class Name: ImportGroupReq
 * Class Description: 导入群组接口请求参数
 *
 * @author liujm
 * @date 2023-03-02
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ImportGroupReq extends RequestBase {

    /**
     * 主键，群ID
     */

    private String groupId;

    /**
     * 群主ID
     */
    private String ownerId;

    /**
     * 群类型，1-私有群，2-公有群
     */
    private Integer groupType;

    /**
     * 群名称
     */
    @NotBlank(message = "群名称不能为空")
    private String groupName;

    /**
     * 是否开启禁言, 1-禁言
     */
    private Boolean mute;

    /**
     * 群状态，1-正常，0-解散
     */
    private Boolean status;

    /**
     * 申请加群选项：0-禁止任何人申请, 1-需要群主或管理员审批, 2-无需审批直接加入
     */
    private Integer applyJoinType;

    /**
     * 群简介
     */
    private String introduction;

    /**
     * 群公告
     */
    private String notification;

    /**
     * 头像
     */
    private String photo;

    /**
     * 最大群成员人数
     */
    private Integer maxMemberCount;

    /**
     * seq
     */
    private Long sequence;

    private Long createTime;

    private Long updateTime;

    private String extra;

}
