package com.russmiles.antifragile;


import com.russmiles.antifragile.configuration.ApplicationConfiguiration;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.MockConsumer;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Properties;

import static junit.framework.TestCase.assertNotNull;

public class ApplicationConfigurationTest {


    private ApplicationConfiguiration applicationConfiguiration;
    private Properties kafkaConsumerProperties;

    private Consumer<String,String> kaffkaConsumer;

    @Before
    public void setup() throws IOException {
        this.applicationConfiguiration = new ApplicationConfiguiration();

        this.kafkaConsumerProperties = this.applicationConfiguiration.getKafkaConsumerProperties();

        kaffkaConsumer = new MockConsumer<>(OffsetResetStrategy.EARLIEST);
    }


    @Test
    public void loadApplicationconfiguration () throws IOException {


        kafkaConsumerProperties.forEach((key,value) -> System.out.println (key + ":" + value ) );

        assertNotNull(kafkaConsumerProperties);
        assert(kafkaConsumerProperties.containsKey("key.deserializer"));
        assert(kafkaConsumerProperties.containsKey("value.deserializer"));


    }

    @Test
    public void loadSimpleKafkaConsumer () throws Exception
    {
        SimpleKafkaConsumer simpleKafkaConsumer = new SimpleKafkaConsumer(kaffkaConsumer);

        assertNotNull(simpleKafkaConsumer);

    }
}
