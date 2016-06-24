package com.russmiles.antifragile;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.Collection;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class SimpleKafkaConsumer {

    //private KafkaConsumer<String,String> kafkaConsumer;
    private Consumer<String,String> kafkaConsumer;


    private ObjectMapper objectMapper = new ObjectMapper();

    private Logger logger = Logger.getLogger(SimpleKafkaConsumer.class.getName());


     @Autowired
    public SimpleKafkaConsumer(Consumer<String, String> kafkaConsumer) {

        this.kafkaConsumer = kafkaConsumer;


        //kafkaConsumer = new KafkaConsumer<>(kafkaConsumerProperties);

        logger.log(Level.INFO, "Created ");


    }

    @PreDestroy
    public void shutdown() {
        logger.log(Level.INFO, "shutdown called");
        kafkaConsumer.close();
    }

    public void ConsumeFromTopic (Collection<String> topics, EventProcessor eventProcessor){
        kafkaConsumer.subscribe(topics);
        try {
            boolean messagesAvailable = true;
            while (messagesAvailable) {
                ConsumerRecords<String, String> records = kafkaConsumer.poll(10000);
                messagesAvailable =  records.count() > 0;
                for (ConsumerRecord<String, String> record : records) {
                    //System.out.println(record.offset() + ": " + record.value());
                    Event event = objectMapper.readValue(record.value(), Event.class);
                    eventProcessor.handleEvent(event);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //kafkaConsumer.close();
        }



    }






}

