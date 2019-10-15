package com.geordie.spring;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.ExecutionException;

@SpringBootApplication
public class SpringCloudApplication {

    private static Logger logger = LogManager.getLogger(SpringCloudApplication.class);

    @Autowired
    private AppLauncher appLauncher;

    static {
//        set eureka properties before SpringApplication start
        System.setProperty("eureka.client.serviceUrl.defaultZone", AppLauncher.EUREKA_URL);
    }

    @PostConstruct
    public void init() throws ExecutionException, InterruptedException {
        appLauncher.start();
    }

    @PreDestroy
    public void destroy() {
        appLauncher.shutdown();
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {

        logger.info("Spring Cloud init");
        SpringApplication.run(SpringCloudApplication.class, args);
        logger.info("Spring Cloud start");
    }
}
