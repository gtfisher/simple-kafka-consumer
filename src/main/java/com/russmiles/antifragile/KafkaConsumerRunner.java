package com.russmiles.antifragile;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@Scope("prototype")
public class KafkaConsumerRunner extends Thread  {


    public KafkaConsumerRunner(@Qualifier("kafkaProducerProperties") Properties kafkaConsumerProperties,
                               CommandProcessor commandProcessor,
                               Collection<String> topics) {
        this.kafkaConsumer = new KafkaConsumer<>(kafkaConsumerProperties);
        this.topics = topics;
        this.commandProcessor = commandProcessor;
    }




    @Value("${kafka.runnable.poll.timeout}")
    private int pollTimeout;

    private ObjectMapper objectMapper = new ObjectMapper();
    private final AtomicBoolean closed = new AtomicBoolean(false);
    private final KafkaConsumer kafkaConsumer;
    private final Collection<String> topics;
    private final CommandProcessor commandProcessor;

    @Override
    public void run() {
        try {
            kafkaConsumer.subscribe(topics);
            while (!closed.get()) {
                ConsumerRecords<String, String> records = kafkaConsumer.poll(pollTimeout);
                for (ConsumerRecord<String, String> record : records) {
                    //System.out.println(record.offset() + ": " + record.value());
                    Command command = objectMapper.readValue(record.value(), Command.class);
                    commandProcessor.handleCommand(command);
                }
            }

        }
        catch (WakeupException  e) {
            if (!closed.get()) throw e;

        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            kafkaConsumer.close();
        }
    }
    // Shutdown hook which can be called from a separate thread
    public void shutdown() {
        closed.set(true);
        kafkaConsumer.wakeup();
    }
}
