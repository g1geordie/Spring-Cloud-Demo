package com.geordie.spring;

import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class PageFailFactory implements FallbackFactory<PageClient> {
    @Override
    public PageClient create(Throwable cause) {
        return new PageClient() {
            @Override
            public String rpage() {
                return "rpage error msg";
            }

            @Override
            public String page() {
                return "page error msg";
            }

            @Override
            public String root() {
                return "root error msg";
            }
        };
    }
}
