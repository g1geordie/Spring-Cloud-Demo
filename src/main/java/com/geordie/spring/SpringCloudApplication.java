package com.geordie.spring;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cloud.deployer.resource.maven.MavenResource;
import org.springframework.cloud.deployer.spi.core.AppDefinition;
import org.springframework.cloud.deployer.spi.core.AppDeploymentRequest;
import org.springframework.cloud.deployer.spi.local.LocalDeployerProperties;
import org.springframework.cloud.deployer.spi.local.LocalTaskLauncher;
import org.springframework.cloud.deployer.spi.task.LaunchState;
import org.springframework.kafka.test.EmbeddedKafkaBroker;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SpringCloudApplication {

    private static final String PORT_KEY = "server.port";
    private static final String EUREKA_URL_KEY = "eureka.client.serviceUrl.defaultZone";
    private static final String EUREKA_URL_FORMAT_FORMAT = "http://localhost:%d/eureka/";
    private static Executor pool = Executors.newFixedThreadPool(5);

    private static LocalTaskLauncher launcher = new LocalTaskLauncher(new LocalDeployerProperties());

    private static Logger logger = LogManager.getLogger(SpringCloudApplication.class);

    public static void main(String[] args) throws InterruptedException, ExecutionException {

        logger.info("Spring Cloud init");
        addShutdownHook();

        EmbeddedKafkaBroker embeddedKafkaBroker = Kafka.getEmbeddedKafkaBroker(3, "test");
        Map<String, String> BUS_CONFIG = Stream.of(new String[][]{
                {"spring.cloud.stream.kafka.binder.brokers", embeddedKafkaBroker.getBrokersAsString()},
                {"spring.cloud.stream.kafka.binder.zkNodes", embeddedKafkaBroker.getZookeeperConnectionString()},
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

        int configPort = 8081;
        int eurekaPort = 8082;
        Map<String, String> eurekaConfig = pair(EUREKA_URL_KEY, String.format(EUREKA_URL_FORMAT_FORMAT, eurekaPort));

        wait(start(createAppDeploymentRequest("spring-config", port(configPort), eurekaConfig, BUS_CONFIG)));
        wait(start(createAppDeploymentRequest("spring-discovery", port(eurekaPort)
                , eurekaConfig
                , pair("spring.cloud.config.uri", String.format("http://localhost:%d", configPort))
        )));

        System.setProperty(EUREKA_URL_KEY, String.format(EUREKA_URL_FORMAT_FORMAT, eurekaPort));
        BootApplication.checkConfigRegisted();

        wait(
                start(createAppDeploymentRequest("spring-app", port(8087), eurekaConfig, BUS_CONFIG)),
                start(createAppDeploymentRequest("spring-app", port(8088), eurekaConfig, BUS_CONFIG)),
                start(createAppDeploymentRequest("spring-app", port(8089), eurekaConfig, BUS_CONFIG)),
                start(createAppDeploymentRequest("spring-feign", port(8084), eurekaConfig)),
                start(createAppDeploymentRequest("spring-gateway", port(8080), eurekaConfig))
        );

        logger.info("Spring Cloud start");

        while (true) {
            Thread.sleep(Long.MAX_VALUE);
        }
    }

    private static void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(
                new Thread(() -> {
                    try {
                        launcher.shutdown();
                    } catch (Exception e) {
                        logger.error("shutdown error , please clean by self");
                    }
                    logger.info("shutdown success");

                })
        );
    }

    private static void waitStatusChange(String id) {
        while (launcher.status(id).getState() == LaunchState.launching) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            logger.debug(String.format("%s status : %s", id, launcher.status(id).getState()));
        }
    }

    private static void wait(CompletableFuture<Void>... completableFutures) throws InterruptedException, ExecutionException {
        CompletableFuture.allOf(completableFutures).get();
    }

    private static CompletableFuture<Void> start(AppDeploymentRequest request, int times) {
        return CompletableFuture.runAsync(() -> {
            int index = 0;
            while (index < times) {
                String id = launcher.launch(request);
                logger.info(String.format("launch the service : %s", id));
                waitStatusChange(id);

                if (launcher.status(id).getState() == LaunchState.failed) {
                    logger.info(String.format("the service failed: %s", id));
                    launcher.cancel(id);
                } else if (launcher.status(id).getState() == LaunchState.running) {
                    logger.info(String.format("service is start : %s ,%s", id, request));
                    break;
                } else {
                    logger.warn("unhandle error");
                }
                index++;
            }
        },pool);
    }

    private static CompletableFuture<Void> start(AppDeploymentRequest request) {
        return start(request, 3);
    }

    private static Map<String, String> port(int port) {
        return pair(PORT_KEY, Integer.toString(port));
    }

    private static Map<String, String> pair(String key, String value) {
        return Collections.singletonMap(key, value);
    }

    @SafeVarargs
    private static AppDeploymentRequest createAppDeploymentRequest(String app, Map<String, String>... properties) {
        MavenResource resource = new MavenResource.Builder()
                .artifactId(app)
                .groupId("com.geordie.spring-cloud")
                .version("0.0.1-SNAPSHOT")
                .build();

        Map<String, String> envs = new HashMap<>();
        Arrays.stream(properties).forEach(envs::putAll);

        AppDefinition definition = new AppDefinition(app, envs);
        return new AppDeploymentRequest(definition, resource);
    }
}
