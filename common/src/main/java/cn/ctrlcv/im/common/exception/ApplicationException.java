package cn.ctrlcv.im.common.exception;

/**
 * Class Name: ApplicationException
 * Class Description: 统一异常处理
 *
 * @author liujm
 * @date 2023-02-03
 */
public class ApplicationException extends RuntimeException {

    private int code;

    private String error;


    public ApplicationException(int code, String message) {
        super(message);
        this.code = code;
        this.error = message;
    }

    public ApplicationException(ApplicationExceptionEnum exceptionEnum) {
        super(exceptionEnum.getError());
        this.code   = exceptionEnum.getCode();
        this.error  = exceptionEnum.getError();
    }

    public int getCode() {
        return code;
    }

    public String getError() {
        return error;
    }


    /**
     *  avoid the expensive and useless stack trace for api exceptions
     *  @see Throwable#fillInStackTrace()
     */
    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
