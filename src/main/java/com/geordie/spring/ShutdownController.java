package com.geordie.spring;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@RestController
public class ShutdownController {

    private static Logger logger = LogManager.getLogger(ShutdownController.class);

    private AtomicBoolean shutdownFlag = new AtomicBoolean(false);
    private AtomicBoolean laucherShutdownFlag = new AtomicBoolean(false);

    @Autowired
    private AppLauncher appLauncher;

    @Autowired
    private ApplicationContext context;

    /**
     * gradle can't forward Ctrl+C command to Gradle SpringCloudRun in Windows ,then shutdownhook cna't be triggered.
     * <p>
     * GetMapping is more convenient than PostMapping for demo .
     */
    @GetMapping("/shutdown")
    public String shutdown() {

        if (shutdownFlag.compareAndSet(false, true)) {
            CompletableFuture.runAsync(this::shutdownAll);
            return "start shutdown";
        } else {
            if (laucherShutdownFlag.get()) {
                return "Http shutdown after 3 second";
            }
            return "shutdown ing ing ing ....";
        }
    }

    private void shutdownAll() {
        logger.info("Spring Cloud is shutdowning .");
//        Although appLauncher shutdown have registed .
//        to prevent http shutdown too early .
        appLauncher.shutdown();
        laucherShutdownFlag.set(true);

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        SpringApplication.exit(context, () -> 1);
        logger.info("Spring Cloud shutdown completely .");
    }
}
