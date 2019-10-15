package com.geordie.spring;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/secure")
public class SecureController {

    @GetMapping("/")
    public String  index() {

        return "This is a secure URL";
    }

    @GetMapping("/user")
    public Principal info(Principal p) {
        return p;
    }
}
