package org.example.picture;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication// 标识这是一个SpringBoot应用
@EnableAsync// 开启异步
@MapperScan("org.example.picture.mapper")// 扫描mapper接口
@EnableAspectJAutoProxy(exposeProxy = true)// 开启AOP
public class PictureApplication {

    public static void main(String[] args) {
        SpringApplication.run(PictureApplication.class, args);
    }

}
