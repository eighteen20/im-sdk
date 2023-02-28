package cn.ctrlcv.im.common.enums;

/**
 * enum Name: ApproveFriendRequestStatusEnum
 * enum Description: 好友审批结果枚举
 *
 * @author liujm
 * @date 2023-02-23
 */
public enum ApproveFriendRequestStatusEnum {

    /**
     * 1 同意；2 拒绝。
     */
    AGREE(1),

    REJECT(2),
    ;

    private int code;

    ApproveFriendRequestStatusEnum(int code){
        this.code=code;
    }

    public int getCode() {
        return code;
    }
}
