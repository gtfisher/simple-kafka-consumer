package com.russmiles.antifragile;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.russmiles.antifragile.api.Command;
import com.russmiles.antifragile.api.CommandProcessor;
import com.russmiles.antifragile.api.Event;
import com.russmiles.antifragile.api.EventProcessor;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.Collection;
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

    public void consumeFromEventTopic(Collection<String> topics, EventProcessor eventProcessor, int pollTimeoutPeriod) {
        kafkaConsumer.subscribe(topics);
        int whileCount = 0;
        int recordsCount = 0;
        try {
            boolean messagesAvailable = true;
            while (messagesAvailable) {
                whileCount++;
                ConsumerRecords<String, String> records = kafkaConsumer.poll(pollTimeoutPeriod);

                if (records.count() < 1) {

                    messagesAvailable = false;
                }
                for (ConsumerRecord<String, String> record : records) {
                    recordsCount++;
                    //System.out.println(record.offset() + ": " + record.value());

                    logger.log(Level.INFO, "record-topic-> {0}", record.topic());
                    logger.log(Level.INFO, "record-value-> {0}", record.value());

                    logger.log(Level.INFO, String.format("offset = %d, key = %s, value= %s ", record.offset(), record.key(),record.value() ));
                    String eventType = record.topic();



                    Event event = objectMapper.readValue(record.value(), Event.class);
                    eventProcessor.handleEvent(event);
                }
            }

            logger.log(Level.INFO, String.format("loop counts-> whileCount-> %s recordsCount-> %s ", whileCount, recordsCount));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //kafkaConsumer.close();
        }
    }


    public void consumeFromCommandTopic(Collection<String> topics, CommandProcessor commandProcessor, int pollTimeoutPeriod){
        kafkaConsumer.subscribe(topics);
        try {
            boolean messagesAvailable = true;

            while (messagesAvailable) {
                logger.log(Level.INFO, "while loop");

                ConsumerRecords<String, String> records = kafkaConsumer.poll(pollTimeoutPeriod);

                logger.log(Level.INFO, "while loop records.count()->" + records.count());


                if (records.count() < 1) {

                    messagesAvailable = false;
                }

                for (ConsumerRecord<String, String> record : records) {

                    logger.log(Level.INFO, "records loop");
                    //System.out.println(record.offset() + ": " + record.value());
                    //Event event = objectMapper.readValue(record.value(), Event.class);
                    logger.log(Level.INFO, String.format("offset = %d, key = %s, value= %s ", record.offset(), record.key(),record.value()));

                    Command command = objectMapper.readValue(record.value(), Command.class);
                    commandProcessor.handleCommand(command);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //kafkaConsumer.close();
        }
    }

}

