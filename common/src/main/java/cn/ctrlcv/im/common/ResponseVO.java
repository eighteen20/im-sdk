package cn.ctrlcv.im.common;

import cn.ctrlcv.im.common.exception.ApplicationExceptionEnum;
import lombok.Data;

/**
 * Class Name: ResponseVO
 * Class Description: 统一的返回对象
 *
 * @author liujm
 * @date 2023-02-03
 */
@Data
public class ResponseVO<T> {

    private Integer code;

    private String msg;

    private T data;

    public ResponseVO(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public ResponseVO() {}

    public static ResponseVO successResponse(Object data) {
        return new ResponseVO<>(200, "success", data);
    }

    public static ResponseVO<?> successResponse() {
        return new ResponseVO<>(200, "success");
    }

    public static ResponseVO<?> errorResponse() {
        return new ResponseVO<>(500, "系统内部异常");
    }

    public static ResponseVO<?> errorResponse(int code, String msg) {
        return new ResponseVO<>(code, msg);
    }

    public static ResponseVO errorResponse(ApplicationExceptionEnum enums) {
        return new ResponseVO<>(enums.getCode(), enums.getError());
    }

    public boolean isOk(){
        return this.code == 200;
    }


    public ResponseVO(int code, String msg) {
        this.code = code;
        this.msg = msg;
//		this.data = null;
    }

    public ResponseVO<?> success(){
        this.code = 200;
        this.msg = "success";
        return this;
    }

    public ResponseVO<T> success(T data){
        this.code = 200;
        this.msg = "success";
        this.data = data;
        return this;
    }

}
