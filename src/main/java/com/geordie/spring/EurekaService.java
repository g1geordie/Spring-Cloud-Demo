package com.geordie.spring;

import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EurekaService {

    @Autowired
    private EurekaClient client;

    public List<String> getMapping() {
        return null;
    }

    public void waitRegister(String serviceName) throws InterruptedException {
        do {

            boolean isPresent = Optional.ofNullable(client.getApplication(serviceName))
                    .map(Application::getInstances)
                    .filter(x -> x.size() > 0)
                    .isPresent();

            if (isPresent)
                break;

            Thread.sleep(2000);
        }
        while (true);
    }
}
