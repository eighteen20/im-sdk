package cn.ctrlcv.im.serve.interceptor;

/**
 * Class Name: RequestHolder
 * Class Description: 请求上下文
 *
 * @author liujm
 * @date 2023-03-21
 */
public class RequestHolder {

    private final static ThreadLocal<Boolean> REQUEST_HOLDER = new ThreadLocal<>();

    public static void set(Boolean isadmin) {
        REQUEST_HOLDER.set(isadmin);
    }

    public static Boolean get() {
        return REQUEST_HOLDER.get();
    }

    public static void remove() {
        REQUEST_HOLDER.remove();
    }
}
