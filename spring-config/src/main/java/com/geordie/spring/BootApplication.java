package com.geordie.spring;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

import java.util.Arrays;
import java.util.stream.Collectors;

@SpringBootApplication
@EnableConfigServer
public class BootApplication {

    public static void main(String[] args) {
        String[] yamls ={
                "config/application.yaml",
                "config/application-bus.yaml",
                "application.yml"
        };
        System.setProperty("spring.config.location",Arrays.stream(yamls).map(x->"classpath:"+x).collect(Collectors.joining(",")));

        SpringApplication.run(BootApplication.class, args);
    }

}
