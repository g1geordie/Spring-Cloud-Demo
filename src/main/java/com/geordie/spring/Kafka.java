package com.geordie.spring;

import org.springframework.kafka.test.EmbeddedKafkaBroker;

public class Kafka {

    public static EmbeddedKafkaBroker getEmbeddedKafkaBroker(int bkNo, String... topics) {

        EmbeddedKafkaBroker embeddedKafkaBroker = new EmbeddedKafkaBroker(bkNo, false, topics);
        embeddedKafkaBroker.afterPropertiesSet();

        return embeddedKafkaBroker;
    }
}
