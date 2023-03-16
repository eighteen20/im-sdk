package cn.ctrlcv.im.serve.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Class Name: ShareThreadPool
 * Class Description: 共享线程池
 *
 * @author liujm
 * @date 2023-03-16
 */
@Slf4j
@Service
public class ShareThreadPool {


    private final ThreadPoolExecutor threadPoolExecutor;

    {
        final AtomicInteger tNum = new AtomicInteger(0);

        threadPoolExecutor = new ThreadPoolExecutor(8, 8, 120, TimeUnit.SECONDS, new LinkedBlockingQueue<>(2 << 20), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setDaemon(true);
                t.setName("SHARE-Processor-" + tNum.getAndIncrement());
                return t;
            }
        });

    }


    private AtomicLong ind = new AtomicLong(0);

    public void submit(Runnable r) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        ind.incrementAndGet();

        threadPoolExecutor.submit(() -> {
            long start = System.currentTimeMillis();
            try {
                r.run();
            } catch (Exception e) {
                log.error("ShareThreadPool_ERROR", e);
            } finally {
                long end = System.currentTimeMillis();
                long dur = end - start;
                long andDecrement = ind.decrementAndGet();
                if (dur > 1000) {
                    log.warn("ShareThreadPool executed taskDone,remanent num = {},slow task fatal warning,costs time = {},stack: {}", andDecrement, dur, stackTrace);
                } else if (dur > 300) {
                    log.warn("ShareThreadPool executed taskDone,remanent num = {},slow task warning: {},costs time = {},", andDecrement, r, dur);
                } else {
                    log.debug("ShareThreadPool executed taskDone,remanent num = {}", andDecrement);
                }
            }
        });


    }
    
}
