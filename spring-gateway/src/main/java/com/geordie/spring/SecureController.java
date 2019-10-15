package com.geordie.spring;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.security.Principal;
import java.util.Collections;
import java.util.Map;

@RestController
public class SecureController {

    @Value("${server.port}")
    private int port;

    private Map<String, String> urlMap;

    @PostConstruct
    private void init() {
        String preffix = String.format("http://localhost:%d", port);
        Map<String, String> map = ImmutableMap.<String, String>builder()
                .put("App - ", "/app/")
                .put("App - page", "/app/page")
                .put("App - rpage", "/app/rpage")
                .put("App - secure", "/secure/")
                .put("App - secure_user", "/secure/user")
                .put("gateway - user", "/user")
                .put("feign ", "/feign")
                .put("feign - hystrix ", "/feign/hystrix")
                .build();

        Map<String, String> urlMap = Maps.newTreeMap();
        map.forEach((k, v) -> {
            urlMap.put(k, preffix + v);
        });
        this.urlMap = Collections.unmodifiableMap(urlMap);
    }

    @GetMapping("/")
    public Map<String, String> index() {
        return urlMap;
    }

    @GetMapping("/user")
    public Principal user(Principal principal) {
        return principal;
    }
}