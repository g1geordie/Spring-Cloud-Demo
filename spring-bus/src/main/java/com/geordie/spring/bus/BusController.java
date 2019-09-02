package com.geordie.spring.bus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.bus.BusProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BusController {

    @Autowired
    private BusProperties busProperties;

    @Autowired
    private ApplicationContext applicationContext;

    @PostMapping("/busMsg")
    public String busMsg(@RequestParam(value = "destination", required = false) String destination) {

        AppRemoteEvent event = new AppRemoteEvent(this ,busProperties.getId(), destination);

        try {
            applicationContext.publishEvent(event);
            return "success";
        } catch (Exception e) {
            return "error";
        }
    }

    @GetMapping("/busTest")
    public String busTest(@RequestParam(value = "destination", required = false) String destination) {
        return busProperties.getId();
    }


}
