package cn.ctrlcv.im.tcp.receiver.process;

/**
 * Class Name: ProcessFactory
 * Class Description: 处理收到的逻辑层MQ消息
 *
 * @author liujm
 * @date 2023-03-21
 */
public class ProcessFactory {

    private static BaseProcess defaultProcess;

    static {
        defaultProcess = new BaseProcess() {
            @Override
            public void processBefore() {

            }

            @Override
            public void processAfter() {

            }
        };
    }

    public static BaseProcess getMessageProcess(Integer command) {
        // TODO 根据command获取对应的处理类
        return defaultProcess;
    }

    public static void processMessage(String message) {
        // TODO 处理收到的逻辑层MQ消息
    }
}
