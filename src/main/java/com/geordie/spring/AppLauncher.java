package com.geordie.spring;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.deployer.resource.maven.MavenResource;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class AppLauncher {

    private static final int configPort = 8081;
    private static final int eurekaPort = 8082;

    private static final String PORT_KEY = "server.port";
    private static final String EUREKA_URL_KEY = "eureka.client.serviceUrl.defaultZone";
    public static final String EUREKA_URL = String.format("http://localhost:%d/eureka/", eurekaPort);

    private static Logger logger = LogManager.getLogger(AppLauncher.class);

    @Autowired
    private Kafka kafka;

    @Autowired
    private EurekaService eurekaService;

    @Autowired
    private MavenLauncher mavenLauncher;

    /**
     * Optional can't be supported in this version 5.1.3.
     * .https://github.com/spring-projects/spring-framework/issues/22139
     */
    @Value("${config.path:#{null}}")
    private String configPath;

    public AppLauncher() {
    }

    public void start() throws ExecutionException, InterruptedException {

        Map<String, String> BUS_CONFIG = getKafkaConfig();
        Map<String, String> EUREKA_CONFIG = pair(EUREKA_URL_KEY, EUREKA_URL);

        Map<String, String> configLoc = Optional.ofNullable(configPath).map(x -> {
            copy(Paths.get(x));
            return pair("spring.cloud.config.server.native.search-locations", "file://" + configPath);
        }).orElse(Collections.emptyMap());
        wait(mavenLauncher.launch("spring-config", port(configPort), EUREKA_CONFIG, BUS_CONFIG, configLoc));

        wait(mavenLauncher.launch("spring-discovery", port(eurekaPort), EUREKA_CONFIG
                , pair("spring.cloud.config.uri", String.format("http://localhost:%d", configPort))
        ));

        eurekaService.waitRegister("CONFIG");

        Map<String, String> authServer = pair("security.url", String.format("localhost:%d", 8888));
        wait(
                mavenLauncher.launch("spring-app", port(8087), EUREKA_CONFIG, BUS_CONFIG, authServer),
                mavenLauncher.launch("spring-app", port(8088), EUREKA_CONFIG, BUS_CONFIG,authServer),
                mavenLauncher.launch("spring-app", port(8089), EUREKA_CONFIG, BUS_CONFIG,authServer),
                mavenLauncher.launch("spring-feign", port(8084), EUREKA_CONFIG),
                mavenLauncher.launch("spring-gateway", port(8080), EUREKA_CONFIG, authServer),
                mavenLauncher.launch("spring-admin", port(9000), EUREKA_CONFIG),
                mavenLauncher.launch("spring-auth", port(8888), EUREKA_CONFIG)
        );

    }

    private Map<String, String> getKafkaConfig() {
        EmbeddedKafkaBroker embeddedKafkaBroker = kafka.getEmbeddedKafkaBroker();
        return Stream.of(new String[][]{
                {"spring.cloud.stream.kafka.binder.brokers", embeddedKafkaBroker.getBrokersAsString()},
                {"spring.cloud.stream.kafka.binder.zkNodes", embeddedKafkaBroker.getZookeeperConnectionString()},
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
    }

    @SafeVarargs
    private final void wait(CompletableFuture<Void>... completableFutures) throws InterruptedException, ExecutionException {
        CompletableFuture.allOf(completableFutures).get();
    }


    private Map<String, String> port(int port) {
        return pair(PORT_KEY, Integer.toString(port));
    }

    private Map<String, String> pair(String key, String value) {
        return Collections.singletonMap(key, value);
    }

    public void shutdown() {
        mavenLauncher.shutdown();
    }

    private void copy(Path to) {

        MavenResource resource = new MavenResource.Builder()
                .artifactId("spring-config")
                .groupId("com.geordie.spring-cloud")
                .version("0.0.1-SNAPSHOT")
                .build();

        try (FileSystem zipfs = FileSystems.newFileSystem(URI.create("jar:" + resource.getFile().toPath().toUri().toString()), Collections.singletonMap("create", true))) {
            Path from = zipfs.getPath("/BOOT-INF/classes/config/");

            Files.walk(from)
                    .forEach((path -> {
                        try {
                            System.out.println(path);
                            Path toPath = to.resolve(from.relativize(path).toString());
                            if (!Files.exists(toPath))
                                Files.copy(path, toPath);
                        } catch (IOException e) {
                            ExceptionUtils.rethrow(e);
                        }
                    }));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
