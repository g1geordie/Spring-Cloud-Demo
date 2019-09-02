package com.geordie.spring;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope
public class RefreshController {

    private static final Logger logger = LogManager.getLogger(RefreshController.class);

    @Value("${page.helloMsg}")
    private String helloMsg;

    @GetMapping("/rpage")
    public String getUsers() {
        logger.info("rpage is called ");
        return helloMsg;
    }
}
