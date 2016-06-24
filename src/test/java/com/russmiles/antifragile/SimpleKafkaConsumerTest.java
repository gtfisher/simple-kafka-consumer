package com.russmiles.antifragile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.russmiles.antifragile.configuration.ApplicationConfiguiration;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.MockConsumer;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.MockProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.Serializer;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Properties;

//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringApplicationConfiguration(classes = KafkaConsumerApplication.class)
public class SimpleKafkaConsumerTest {

  //  @Autowired
  //  @Qualifier("kafkaConsumerProperties")
    Properties kafkaConsumerProperties;

    ApplicationConfiguiration applicationConfiguiration;

 //   @Autowired
 //   @Qualifier("kafkaProducerProperties")
    Properties kafkaProducerProperties;

    //@Value("${kafka.employee.event.topic}")
    private String employeeEventTopic;

    private boolean eventHandlerCalled = false;

    //private MockConsumer<String, String> kafkaMockConsumer = new MockConsumer<>(OffsetResetStrategy.EARLIEST);
   // @Autowired
    private SimpleKafkaConsumer simpleKafkaConsumer;

    private KafkaConsumer<String,String> kafkaConsumer;

    @Before
    public void setup () throws Exception {

        applicationConfiguiration = new ApplicationConfiguiration();

        kafkaConsumerProperties = applicationConfiguiration.getKafkaConsumerProperties();

        kafkaProducerProperties = applicationConfiguiration.getKafkaProducerProperties();

        employeeEventTopic = "EmployeeEvents";

        EmployeeDTO employeeDTO = new EmployeeDTO("Bilbo", "Baggins");

        postToKafka(employeeDTO,employeeEventTopic);

        kafkaConsumer = new KafkaConsumer<String, String>(kafkaConsumerProperties);

        simpleKafkaConsumer = new SimpleKafkaConsumer(kafkaConsumer);
    }


    @Test
    public void consumeFromTopic() throws Exception {


        EventProcessor eventProcessor = new EventProcessor();

        simpleKafkaConsumer.ConsumeFromTopic(Arrays.asList(employeeEventTopic),eventProcessor);

    }

    public class EventProcessor implements com.russmiles.antifragile.EventProcessor {

        @Override
        public void handleEvent(Event event) {

            System.out.println ("hanlde Event called->" + event.getEventType());
            eventHandlerCalled = true;
        }
    }

    public void postToKafka(EmployeeDTO employeeDTO, String topicToPostTo) throws Exception {


        KafkaProducer<String, String> producer;


        producer = new KafkaProducer<>(kafkaProducerProperties);


        ObjectMapper objectMapper = new ObjectMapper();

        LinkedHashMap<String,Object> employeeMap = new LinkedHashMap<>();
        employeeMap.put("employeeDTO",employeeDTO);

        Event event = new Event("EmployeeCreated", employeeMap);

        String jsonEventString = objectMapper.writeValueAsString(event);

        try {


            producer.send(new ProducerRecord<String, String>(topicToPostTo, jsonEventString));
        }
        catch (Throwable throwable){
            System.out.printf("%s", throwable.getStackTrace());
        }
        finally {
            producer.close();
        }
    }




}
