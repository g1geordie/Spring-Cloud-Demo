package com.geordie.spring;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

@RestController
public class PageController {

    private static final Logger logger = LogManager.getLogger(PageController.class);

    @Value("${page.helloMsg}")
    private String helloMsg;

    @GetMapping("/page")
    public String getUsers() {
        logger.info("page is called ");
        return helloMsg;
    }
}
