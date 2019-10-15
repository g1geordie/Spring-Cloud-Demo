package com.geordie.spring;

import feign.hystrix.FallbackFactory;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.stereotype.Component;

@Component
public class PageFailFactory implements FallbackFactory<PageClient> {
    @Override
    public PageClient create(Throwable cause) {
        return new PageClient() {
            @Override
            public String rpage() {

                return String.format("rpage error msg : %s", ExceptionUtils.getFullStackTrace(cause));
            }

            @Override
            public String page() {
                return String.format("page error msg : %s", ExceptionUtils.getFullStackTrace(cause));

            }

            @Override
            public String root() {
                return String.format("root error msg : %s", ExceptionUtils.getFullStackTrace(cause));

            }
        };
    }
}
