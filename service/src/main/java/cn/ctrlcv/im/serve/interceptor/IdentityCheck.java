package cn.ctrlcv.im.serve.interceptor;

import cn.ctrlcv.im.common.enums.BaseErrorCodeEnum;
import cn.ctrlcv.im.common.model.ResponseVO;
import cn.ctrlcv.im.common.config.ImConfig;
import cn.ctrlcv.im.common.constant.Constants;
import cn.ctrlcv.im.common.enums.GateWayErrorCodeEnum;
import cn.ctrlcv.im.common.enums.ImUserTypeEnum;
import cn.ctrlcv.im.common.exception.ApplicationExceptionEnum;
import cn.ctrlcv.im.common.utils.SigApi;
import cn.ctrlcv.im.serve.user.dao.ImUserDataEntity;
import cn.ctrlcv.im.serve.user.service.IUserService;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * Class Name: IdentityCheck
 * Class Description: 身份校验
 *
 * @author liujm
 * @date 2023-03-21
 */
@Slf4j
@Component
public class IdentityCheck {

    @Resource
    private IUserService imUserService;

    @Resource
    private ImConfig imConfig;

    @Resource
    private StringRedisTemplate stringRedisTemplate;


    private static final long ONE_THOUSAND = 1000;


    public ApplicationExceptionEnum checkUserSig(String identifier,
                                                 String appId, String userSig) {

        String cacheUserSig = stringRedisTemplate.opsForValue()
                .get(appId + ":" + Constants.RedisKey.USER_SIGN + ":"
                        + identifier + userSig);
        if (!StringUtils.isBlank(cacheUserSig) && Long.parseLong(cacheUserSig)
                > System.currentTimeMillis() / 1000) {
            this.setIsAdmin(identifier, Integer.valueOf(appId));
            return BaseErrorCodeEnum.SUCCESS;
        }

        //获取秘钥
        String privateKey = imConfig.getPrivateKey();

        //根据appid + 秘钥创建sigApi
        SigApi sigAPI = new SigApi(Long.parseLong(appId), privateKey);

        //调用sigApi对userSig解密
        JSONObject jsonObject = SigApi.decodeUserSig(userSig);

        //取出解密后的appid 和 操作人 和 过期时间做匹配，不通过则提示错误
        long expireTime = 0L;
        long expireSec = 0L;
        long time = 0L;
        String decoerAppId = "";
        String decoderidentifier = "";

        try {
            decoerAppId = jsonObject.getString("TLS.appId");
            decoderidentifier = jsonObject.getString("TLS.identifier");
            String expireStr = jsonObject.get("TLS.expire").toString();
            String expireTimeStr = jsonObject.get("TLS.expireTime").toString();
            time = Long.parseLong(expireTimeStr);
            expireSec = Long.parseLong(expireStr);
            expireTime = Long.parseLong(expireTimeStr) + expireSec;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("checkUserSig-error:{}", e.getMessage());
        }

        if (!decoderidentifier.equals(identifier)) {
            return GateWayErrorCodeEnum.USER_SIGN_OPERATE_NOT_MATE;
        }

        if (!decoerAppId.equals(appId)) {
            return GateWayErrorCodeEnum.USER_SIGN_IS_ERROR;
        }

        if (expireSec == 0L) {
            return GateWayErrorCodeEnum.USER_SIGN_IS_EXPIRED;
        }

        if (expireTime < System.currentTimeMillis() / ONE_THOUSAND) {
            return GateWayErrorCodeEnum.USER_SIGN_IS_EXPIRED;
        }

        //appid + "xxx" + userId + sign
        String genSig = sigAPI.genUserSig(identifier, expireSec, time, null);
        if (genSig.equalsIgnoreCase(userSig)) {
            String key = appId + ":" + Constants.RedisKey.USER_SIGN + ":" + identifier + userSig;

            long etime = expireTime - System.currentTimeMillis() / 1000;
            stringRedisTemplate.opsForValue().set(
                    key, Long.toString(expireTime), etime, TimeUnit.SECONDS
            );
            this.setIsAdmin(identifier, Integer.valueOf(appId));
            return BaseErrorCodeEnum.SUCCESS;
        }

        return GateWayErrorCodeEnum.USER_SIGN_IS_ERROR;
    }


    /**
     * 根据appid,identifier判断是否App管理员,并设置到RequestHolder
     *
     * @param identifier 用户名
     * @param appId      应用id
     */
    public void setIsAdmin(String identifier, Integer appId) {
        //去DB或Redis中查找, 后面写
        ResponseVO<ImUserDataEntity> singleUserInfo = imUserService.getSingleUserInfo(identifier, appId);
        if (singleUserInfo.isOk()) {
            RequestHolder.set(singleUserInfo.getData().getUserType() == ImUserTypeEnum.APP_ADMIN.getCode());
        } else {
            RequestHolder.set(false);
        }
    }

}
