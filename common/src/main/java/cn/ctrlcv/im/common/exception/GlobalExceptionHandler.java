package cn.ctrlcv.im.common.exception;

import cn.ctrlcv.im.common.enums.BaseErrorCodeEnum;
import cn.ctrlcv.im.common.model.ResponseVO;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Objects;
import java.util.Set;

/**
 * Class Name: GlobalExceptionHandler
 * Class Description: 统一异常处理
 *
 * @author liujm
 * @date 2023-02-07
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResponseVO<?> unknownException(Exception e) {
        e.printStackTrace();
        ResponseVO<?> resultBean = new ResponseVO<>();
        resultBean.setCode(BaseErrorCodeEnum.SYSTEM_ERROR.getCode());
        resultBean.setMsg(BaseErrorCodeEnum.SYSTEM_ERROR.getError());
        /*
         * 未知异常的话，这里写逻辑，发邮件，发短信都可以、、
         */
        return resultBean;
    }


    /**
     * Validator 参数校验异常处理
     *
     * @param ex {@link ConstraintViolationException}
     * @return {@link Object}
     */
    @ExceptionHandler(value = ConstraintViolationException.class)
    @ResponseBody
    public Object handleMethodArgumentNotValidException(ConstraintViolationException ex) {

        Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations();
        ResponseVO<?> resultBean = new ResponseVO<>();
        resultBean.setCode(BaseErrorCodeEnum.PARAMETER_ERROR.getCode());
        for (ConstraintViolation<?> constraintViolation : constraintViolations) {
            PathImpl pathImpl = (PathImpl) constraintViolation.getPropertyPath();
            // 读取参数字段，constraintViolation.getMessage() 读取验证注解中的message值
            String paramName = pathImpl.getLeafNode().getName();
            String message = "参数{".concat(paramName).concat("}").concat(constraintViolation.getMessage());
            resultBean.setMsg(message);

            return resultBean;
        }
        resultBean.setMsg(BaseErrorCodeEnum.PARAMETER_ERROR.getError() + ex.getMessage());
        return resultBean;
    }

    @ExceptionHandler(ApplicationException.class)
    @ResponseBody
    public Object applicationExceptionHandler(ApplicationException e) {
        // 使用公共的结果类封装返回结果, 这里我指定状态码为
        ResponseVO<?> resultBean = new ResponseVO<>();
        resultBean.setCode(e.getCode());
        resultBean.setMsg(e.getError());
        return resultBean;
    }

    /**
     * Validator 参数校验异常处理
     *
     * @param ex {@link BindException}
     * @return {@link Object}
     */
    @ExceptionHandler(value = BindException.class)
    @ResponseBody
    public Object handleException2(BindException ex) {
        FieldError err = ex.getFieldError();
        assert err != null;
        String message = "参数{".concat(err.getField()).concat("}").concat(Objects.requireNonNull(err.getDefaultMessage()));
        ResponseVO<?> resultBean = new ResponseVO<>();
        resultBean.setCode(BaseErrorCodeEnum.PARAMETER_ERROR.getCode());
        resultBean.setMsg(message);
        return resultBean;


    }

    /**
     * 方法参数校验异常
     *
     * @param ex {@link MethodArgumentNotValidException}
     * @return {@link Object}
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseBody
    public Object handleException1(MethodArgumentNotValidException ex) {
        StringBuilder errorMsg = new StringBuilder();
        BindingResult re = ex.getBindingResult();
        for (ObjectError error : re.getAllErrors()) {
            errorMsg.append(error.getDefaultMessage()).append(",");
        }
        errorMsg.delete(errorMsg.length() - 1, errorMsg.length());

        ResponseVO<?> resultBean = new ResponseVO<>();
        resultBean.setCode(BaseErrorCodeEnum.PARAMETER_ERROR.getCode());
        resultBean.setMsg(BaseErrorCodeEnum.PARAMETER_ERROR.getError() + " : " + errorMsg);
        return resultBean;
    }

}
