package com.geordie.spring;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "app", fallbackFactory = PageFailFactory.class)
public interface PageClient {

    @RequestMapping(method = RequestMethod.GET, value = "/rpage")
    String rpage();

    @RequestMapping(method = RequestMethod.GET, value = "/page")
    String page();

    @RequestMapping(method = RequestMethod.GET, value = "/")
    String root();
}
