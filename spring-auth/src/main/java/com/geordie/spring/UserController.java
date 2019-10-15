package com.geordie.spring;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class UserController {

    @RequestMapping(value = "/user/me", method = {RequestMethod.GET, RequestMethod.POST})
    public Principal user(Principal principal) {
        return principal;
    }
}