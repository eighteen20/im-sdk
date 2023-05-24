package cn.ctrlcv.im.tcp.receiver;

import cn.ctrlcv.im.codec.pack.MessagePack;
import cn.ctrlcv.im.common.ClientTypeEnum;
import cn.ctrlcv.im.common.constant.Constants;
import cn.ctrlcv.im.common.enums.DeviceMultiLoginEnum;
import cn.ctrlcv.im.common.enums.command.SystemCommand;
import cn.ctrlcv.im.common.model.UserClientDTO;
import cn.ctrlcv.im.tcp.redis.RedisManager;
import cn.ctrlcv.im.tcp.utils.SessionSocketHolder;
import com.alibaba.fastjson.JSONObject;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RTopic;
import org.redisson.api.listener.MessageListener;

import java.util.List;

/**
 * Class Name: UserLoginMessageListener
 * Class Description: 多端同步，根据设定的登录模式进行踢下线等操作
 *
 * <p>
 *     <p>具体逻辑：</p>
 *     <p>1 - 单端登录。踢掉除了本 clientType + imei 的设备</p>
 *     <p>2 - 双端登录：允许pc/mobile 其中一端登录 + web端 踢掉除了本 clientType + imei 以外的web端设备</p>
 *     <p>3 - 三端登录：允许手机+pc+web，踢掉同端的其他imei 除了web</p>
 *     <p>4 - 不做任何处理</p>
 * </p>
 *
 * @author liujm
 * @date 2023-03-15
 */
@Slf4j
public class UserLoginMessageListener {

    private Integer loginModel;

    public UserLoginMessageListener(Integer loginModel) {
        this.loginModel = loginModel;
    }

    public void listenerUserLogin() {
        RTopic topic = RedisManager.getRedissonClient().getTopic(Constants.RedisKey.USER_LOGIN_CHANNEL);
        topic.addListener(String.class, new MessageListener<String>() {
            @Override
            public void onMessage(CharSequence charSequence, String msg) {
                log.info(" ========== 监听到用户上线，用户信息：{} ========== ", msg);
                UserClientDTO dto = JSONObject.parseObject(msg, UserClientDTO.class);
                List<NioSocketChannel> channels = SessionSocketHolder.get(dto.getAppId(), dto.getUserId());

                String unique = dto.getClientType() + ":" + dto.getImei();

                for (NioSocketChannel channel : channels) {
                    Integer clientType = (Integer) channel.attr(AttributeKey.valueOf(Constants.CLIENT_TYPE)).get();
                    String imei = (String) channel.attr(AttributeKey.valueOf(Constants.IMEI)).get();

                    if (loginModel == DeviceMultiLoginEnum.ONE.getLoginMode()) {

                        if (!(clientType + ":" + imei).equals(unique)) {
                            // 踢掉客户端
                            notifyOffline(channel);
                        }

                    } else if (loginModel == DeviceMultiLoginEnum.TWO.getLoginMode()) {
                        if (ClientTypeEnum.WEB.getCode() == dto.getClientType()) {
                            continue;
                        }
                        if (ClientTypeEnum.WEB.getCode() == clientType) {
                            continue;
                        }

                        if (!(clientType + ":" + imei).equals(unique)) {
                            // 踢掉客户端
                            notifyOffline(channel);
                        }
                    } else if (loginModel == DeviceMultiLoginEnum.THREE.getLoginMode()) {
                        if (ClientTypeEnum.WEB.getCode() == dto.getClientType()) {
                            continue;
                        }
                        // 是否是 同类型客户端（eg: ios 和 Android 属于通断）
                        boolean isSameClient = false;

                        boolean oldPhone = ClientTypeEnum.IOS.getCode() == clientType || ClientTypeEnum.ANDROID.getCode() == clientType;
                        boolean newPhone = ClientTypeEnum.IOS.getCode() == dto.getClientType() || ClientTypeEnum.ANDROID.getCode() == dto.getClientType();
                        if (oldPhone && newPhone) {
                            // 已登录的客户端 和 新登录的都是手机端
                            isSameClient = true;
                        }
                        boolean oldPc = ClientTypeEnum.WINDOWS.getCode() == clientType || ClientTypeEnum.MAC.getCode() == clientType;
                        boolean newPc = ClientTypeEnum.WINDOWS.getCode() == dto.getClientType() || ClientTypeEnum.MAC.getCode() == dto.getClientType();
                        if (oldPhone && newPhone) {
                            // 已登录的客户端 和 新登录的都是PC端
                            isSameClient = true;
                        }

                        if (isSameClient && !(clientType + ":" + imei).equals(unique)) {
                            // 踢掉客户端
                            notifyOffline(channel);
                        }

                    }
                }
            }
        });
    }

    private static void notifyOffline(NioSocketChannel channel) {
        MessagePack<Object> pack = new MessagePack<>();
        pack.setCommand(SystemCommand.MUTUAL_LOGIN.getCommand());
        pack.setToId((String) channel.attr(AttributeKey.valueOf(Constants.USER_ID)).get());
        pack.setUserId((String) channel.attr(AttributeKey.valueOf(Constants.USER_ID)).get());
        channel.writeAndFlush(pack);
    }

}
