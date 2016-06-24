package com.russmiles.antifragile;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.MockConsumer;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.MockProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.Serializer;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = KafkaConsumerApplication.class)
public class SimpleKafkaMockConsumerTest {

    @Autowired
    @Qualifier("kafkaConsumerProperties")
    Properties kafkaConsumerProperties;

    @Autowired
    @Qualifier("kafkaProducerProperties")
    Properties kafkaProducerProperties;

    @Value("${kafka.employee.event.topic}")
    private String employeeEventTopic;

    private boolean eventHandlerCalled = false;

    private MockConsumer<String, String> kafkaMockConsumer = new MockConsumer<>(OffsetResetStrategy.EARLIEST);

    @Test
    @Ignore
    public void postAndConsumeFromTopic() throws Exception {



        EmployeeDTO employeeDTO = new EmployeeDTO("Bilbo", "Baggins");


        //postToKafka(employeeDTO, employeeEventTopic);

        ObjectMapper objectMapper = new ObjectMapper();

        String jsonEmployeeString = objectMapper.writeValueAsString(employeeDTO);


        TopicPartition topicPartition = new TopicPartition(employeeEventTopic,0);
        kafkaMockConsumer.assign(Arrays.asList(topicPartition));

        ConsumerRecord<String,String> record = new ConsumerRecord<String, String>(employeeEventTopic,0,0,null,jsonEmployeeString);

        kafkaMockConsumer.addRecord(record);


        SimpleKafkaConsumer simpleKafkaConsumer = new SimpleKafkaConsumer(kafkaMockConsumer);




        EventProcessor eventProcessor = new EventProcessor();



        simpleKafkaConsumer.ConsumeFromTopic(Arrays.asList(employeeEventTopic),eventProcessor);

    }

    public class EventProcessor implements com.russmiles.antifragile.EventProcessor {

        @Override
        public void handleEvent(Event event) {
            eventHandlerCalled = true;
        }
    }

    public void postToKafka(EmployeeDTO employeeDTO, String topicToPostTo) throws Exception {


        //KafkaProducer<String, String> producer;

        Serializer<String> stringSerializer = new org.apache.kafka.common.serialization.StringSerializer();
        MockProducer<String,String> producer = new MockProducer<String,String>(true,stringSerializer,stringSerializer);

        //producer = new KafkaProducer<>(kafkaProducerProperties);


        ObjectMapper objectMapper = new ObjectMapper();

        String jsonEmployeeString = objectMapper.writeValueAsString(employeeDTO);

        try {


            producer.send(new ProducerRecord<String, String>(topicToPostTo, jsonEmployeeString));
        }
        catch (Throwable throwable){
            System.out.printf("%s", throwable.getStackTrace());
        }
        finally {
            producer.close();
        }
    }


}
