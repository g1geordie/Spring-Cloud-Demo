package com.geordie.spring;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class IndexController {

    private static final Logger logger = LogManager.getLogger(IndexController.class);

    @GetMapping("/")
    public String index(HttpServletRequest request) {
        return    "hello , "+request.getServerName()+":"+request.getServerPort();
    }
}
