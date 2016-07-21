package com.russmiles.antifragile;


import com.russmiles.antifragile.configuration.ApplicationConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.MockConsumer;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.Properties;

import static junit.framework.TestCase.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(ApplicationConfiguration.class)
public class ApplicationConfigurationTest {

    private static Log log = LogFactory.getLog(ApplicationConfigurationTest.class.getName());



    private ApplicationConfiguration applicationConfiguration;


    private Properties kafkaConsumerProperties;

    private Consumer<String,String> kaffkaConsumer;

    @Before
    public void setup() throws IOException {
        this.applicationConfiguration = new ApplicationConfiguration();
        log.info(String.format("application configuration-> {}", "ApplicationConfiguration.EMPLOYEE_COMMAND_TOPIC"));

        this.kafkaConsumerProperties = this.applicationConfiguration.getKafkaConsumerProperties();

        kaffkaConsumer = new MockConsumer<>(OffsetResetStrategy.EARLIEST);
    }

    @Test
    public void contextLoads() {
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
