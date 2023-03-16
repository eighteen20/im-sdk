package cn.ctrlcv.im.common.config;

import lombok.Data;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Class Name: GlobalHttpClientConfig
 * Class Description: http客户端参数配置
 *
 * @author liujm
 * @date 2023-03-16
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "httpclient")
public class GlobalHttpClientConfig {

    /**
     * 最大连接数
     */
    private Integer maxTotal;

    /**
     * 最大并发链接数
     */
    private Integer defaultMaxPerRoute;

    /**
     * 创建链接的最大时间
     */
    private Integer connectTimeout;

    /**
     * 链接获取超时时间
     */
    private Integer connectionRequestTimeout;

    /**
     * 数据传输最长时间
     */
    private Integer socketTimeout;

    /**
     * 提交时检查链接是否可用
     */
    private boolean staleConnectionCheckEnabled;

    PoolingHttpClientConnectionManager manager = null;
    HttpClientBuilder httpClientBuilder = null;

    /**
     * 定义httpClient链接池
     *
     * @return
     */
    @Bean(name = "httpClientConnectionManager")
    public PoolingHttpClientConnectionManager getPoolingHttpClientConnectionManager() {
        return getManager();
    }

    private PoolingHttpClientConnectionManager getManager() {
        if (manager != null) {
            return manager;
        }
        manager = new PoolingHttpClientConnectionManager();
        // 设定最大链接数
        manager.setMaxTotal(maxTotal);
        // 设定并发链接数
        manager.setDefaultMaxPerRoute(defaultMaxPerRoute);
        return manager;
    }

    /**
     * 实例化连接池，设置连接池管理器。 这里需要以参数形式注入上面实例化的连接池管理器
     *
     * @Qualifier 指定bean标签进行注入
     */
    @Bean(name = "httpClientBuilder")
    public HttpClientBuilder getHttpClientBuilder(
            @Qualifier("httpClientConnectionManager") PoolingHttpClientConnectionManager httpClientConnectionManager) {

        // HttpClientBuilder中的构造方法被protected修饰，所以这里不能直接使用new来实例化一个HttpClientBuilder,
        // 可以使用HttpClientBuilder提供的静态方法create()来获取HttpClientBuilder对象
        httpClientBuilder = HttpClientBuilder.create();
        httpClientBuilder.setConnectionManager(httpClientConnectionManager);
        return httpClientBuilder;
    }


    /**
     * 注入连接池，用于获取httpClient
     *
     * @param httpClientBuilder
     * @return
     */
    @Bean
    public CloseableHttpClient getCloseableHttpClient(
            @Qualifier("httpClientBuilder") HttpClientBuilder httpClientBuilder) {

        return httpClientBuilder.build();
    }

    public CloseableHttpClient getCloseableHttpClient() {
        if (httpClientBuilder != null) {
            return httpClientBuilder.build();
        }
        httpClientBuilder = HttpClientBuilder.create();
        httpClientBuilder.setConnectionManager(getManager());
        return httpClientBuilder.build();
    }

    /**
     * Builder是RequestConfig的一个内部类 通过RequestConfig的custom方法来获取到一个Builder对象
     * 设置builder的连接信息
     *
     * @return
     */
    @Bean(name = "builder")
    public RequestConfig.Builder getBuilder() {
        RequestConfig.Builder builder = RequestConfig.custom();
        return builder.setConnectTimeout(connectTimeout).setConnectionRequestTimeout(connectionRequestTimeout)
                .setSocketTimeout(socketTimeout).setStaleConnectionCheckEnabled(staleConnectionCheckEnabled);
    }

    /**
     * 使用builder构建一个RequestConfig对象
     *
     * @param builder
     * @return
     */
    @Bean
    public RequestConfig getRequestConfig(@Qualifier("builder") RequestConfig.Builder builder) {
        return builder.build();
    }
}
