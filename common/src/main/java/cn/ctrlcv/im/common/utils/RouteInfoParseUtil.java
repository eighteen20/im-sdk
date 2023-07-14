package cn.ctrlcv.im.common.utils;

import cn.ctrlcv.im.common.enums.BaseErrorCodeEnum;
import cn.ctrlcv.im.common.exception.ApplicationException;
import cn.ctrlcv.im.common.route.RouteInfo;

/**
 * Class Name: RouteInfoParseUtil
 * Class Description: route节点解析工具
 *
 * @author liujm
 * @date 2023-03-15
 */
public class RouteInfoParseUtil {

    /**
     * 将获取到的结点转为节点信息对象
     *
     * @param info
     * @return
     */
    public static RouteInfo parseBean(String info){
        try {
            String[] serverInfo = info.split(":");
            return new RouteInfo(serverInfo[0], Integer.parseInt(serverInfo[1]));
        }catch (Exception e){
            throw new ApplicationException(BaseErrorCodeEnum.PARAMETER_ERROR) ;
        }
    }


}
