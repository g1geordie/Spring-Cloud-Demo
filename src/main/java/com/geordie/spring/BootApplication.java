package com.geordie.spring;


import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Optional;

@SpringBootApplication
public class BootApplication {

    private static Logger logger = LogManager.getLogger(BootApplication.class);

    public static void checkConfigRegisted() throws InterruptedException {

        try (ConfigurableApplicationContext ctx = new SpringApplicationBuilder(BootApplication.class)
                .web(WebApplicationType.NONE).run()) {

            logger.info("Spring Boot application started");

            EurekaClient eurekaClient = ctx.getBean(EurekaClient.class);
            checkConfigRegisted(eurekaClient);

            logger.info("Spring Config Server has registed");

        }
    }

    private static void checkConfigRegisted(EurekaClient client) throws InterruptedException {

        do {

            boolean isPresent = Optional.ofNullable(client.getApplication("CONFIG"))
                    .map(Application::getInstances)
                    .filter(x -> x.size() > 0)
                    .isPresent();

            if (isPresent)
                break;

            Thread.sleep(2000);
        }
        while (true);
    }

}
