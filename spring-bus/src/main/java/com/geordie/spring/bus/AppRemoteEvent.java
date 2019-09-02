package com.geordie.spring.bus;

import org.springframework.cloud.bus.event.RemoteApplicationEvent;
import org.springframework.stereotype.Component;

@Component
public class AppRemoteEvent extends RemoteApplicationEvent {

    private String message;


    public AppRemoteEvent() {
    }

    public AppRemoteEvent(Object source, String originService, String message) {

        super(source, originService);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public AppRemoteEvent setMessage(String message) {
        this.message = message;
        return this;
    }
}