package cn.ctrlcv.im.messagestore;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author ljm19
 */
@SpringBootApplication
@MapperScan("cn.ctrlcv.im.messagestore.dao.mapper")
public class MessageStoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(MessageStoreApplication.class, args);
    }

}
