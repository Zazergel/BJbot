package com.zazergel.bjbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class BJBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(BJBotApplication.class, args);
    }

}
