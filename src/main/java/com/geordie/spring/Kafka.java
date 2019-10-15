package com.geordie.spring;

import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Lazy
public class Kafka {

    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @PostConstruct
    private void init() {
        EmbeddedKafkaBroker embeddedKafkaBroker = getEmbeddedKafkaBroker(3, "test");
        Map<String, String> BUS_CONFIG = Stream.of(new String[][]{
                {"spring.cloud.stream.kafka.binder.brokers", embeddedKafkaBroker.getBrokersAsString()},
                {"spring.cloud.stream.kafka.binder.zkNodes", embeddedKafkaBroker.getZookeeperConnectionString()},
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

        this.embeddedKafkaBroker = embeddedKafkaBroker;
    }

    @PreDestroy
    private void destroy() {
        embeddedKafkaBroker.destroy();
    }

    public EmbeddedKafkaBroker getEmbeddedKafkaBroker() {
        return embeddedKafkaBroker;
    }


    private static EmbeddedKafkaBroker getEmbeddedKafkaBroker(int bkNo, String... topics) {

        EmbeddedKafkaBroker embeddedKafkaBroker = new EmbeddedKafkaBroker(bkNo, false, topics);
        embeddedKafkaBroker.afterPropertiesSet();

        return embeddedKafkaBroker;
    }
}
