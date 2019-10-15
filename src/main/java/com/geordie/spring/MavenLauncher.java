package com.geordie.spring;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cloud.deployer.resource.maven.MavenResource;
import org.springframework.cloud.deployer.spi.core.AppDefinition;
import org.springframework.cloud.deployer.spi.core.AppDeploymentRequest;
import org.springframework.cloud.deployer.spi.local.LocalDeployerProperties;
import org.springframework.cloud.deployer.spi.local.LocalTaskLauncher;
import org.springframework.cloud.deployer.spi.task.LaunchState;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class MavenLauncher {

    private LocalTaskLauncher launcher = new LocalTaskLauncher(new LocalDeployerProperties());
    private ExecutorService pool = Executors.newFixedThreadPool(5);
    private static Logger logger = LogManager.getLogger(MavenLauncher.class);

    @SafeVarargs
    public final CompletableFuture<Void> launch(String app, Map<String, String>... properties) {
        return launch(createAppDeploymentRequest(app, properties));
    }

    private CompletableFuture<Void> launch(AppDeploymentRequest request, int times) {
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
        }, pool);
    }

    public void shutdown() {
        try {
            launcher.shutdown();
            pool.shutdown();
        } catch (Exception e) {
            logger.error("shutdown error , please clean by self");
        }
        logger.info("shutdown success");
    }


    private void waitStatusChange(String id) {
        while (launcher.status(id).getState() == LaunchState.launching) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            logger.debug(String.format("%s status : %s", id, launcher.status(id).getState()));
        }
    }

    private CompletableFuture<Void> launch(AppDeploymentRequest request) {
        return launch(request, 3);
    }

    @SafeVarargs
    private final AppDeploymentRequest createAppDeploymentRequest(String app, Map<String, String>... properties) {
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
