package com.russmiles.antifragile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.russmiles.antifragile.api.Command;
import com.russmiles.antifragile.api.Event;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

@Component
public class SimpleKafkaProducer {





    private Producer<String,String> kafkaProducer;
    private final String kafkaCommandTopic;
    private final String kafkaEventTopic;

    private ObjectMapper objectMapper = new ObjectMapper();



    private Log log = LogFactory.getLog(getClass());

    @Autowired
    public SimpleKafkaProducer(Producer<String, String> kafkaProducer, @Value("kafka.employee.command.topic") String commandTopic,  @Value("kafka.employee.event.topic")String eventTopic ) {

        this.kafkaProducer = kafkaProducer;
        this.kafkaCommandTopic = commandTopic;
        this.kafkaEventTopic = eventTopic;

        log.info ("created");

    }

    private void postToKafka(String topic, String data)
    {
        log.info (String.format("postToKafka topic {%s} data-{%s}",topic, data));
        try {
            kafkaProducer.send(new ProducerRecord<String, String>(topic, data));
        }
        catch (Throwable throwable){
            log.fatal("kafkaProducerError", throwable);
        }

    }

    public void postToKafka(Command command) {
        try {
            log.info("post command to kafka");
            String jsonEventString = objectMapper.writeValueAsString(command);

            postToKafka(this.kafkaCommandTopic,jsonEventString);

        }
        catch (JsonProcessingException jsonProcessingException)
        {
            log.error(jsonProcessingException);
        }


    }

    public void postToKafka(Event event, String topicToPostTo)  {

        try {

            log.info("post event to kafka");

            String jsonEventString = objectMapper.writeValueAsString(event);

            postToKafka(this.kafkaEventTopic,jsonEventString);

        }
        catch (JsonProcessingException jsonProcessingException)
        {
            log.error(jsonProcessingException);
        }


    }

    @PreDestroy
    public void shutdown() {
        log.info("shutdown called");
        kafkaProducer.close();
    }
}
