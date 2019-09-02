package com.geordie.spring;

import com.google.common.collect.ImmutableMap;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
public class IndexController {

    @Autowired
    private PageClient client;

    @GetMapping("/")
    public Map<String, String> index() {
        return ImmutableMap.<String, String> builder()
                .put("root", client.root())
                .put("page",  client.page())
                .put("rpage", client.rpage())
                .build();
    }

    @HystrixCommand(fallbackMethod = "fallbackMethod")
    @GetMapping("/hystrix")
    public Map<String, String> hystrix() throws InterruptedException {

        Map<String, String> map =ImmutableMap.<String, String> builder()
                .put("root", client.root())
                .put("page",  client.page())
                .put("rpage", client.rpage())
                .build();
        Thread.sleep(5000);

        return map;
    }

    public Map<String, String> fallbackMethod (){
        return Collections.singletonMap("error","notice your attitude");
    }
}
