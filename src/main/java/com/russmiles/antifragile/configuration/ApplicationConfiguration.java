package com.russmiles.antifragile.configuration;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

@Configuration
public class ApplicationConfiguration {

    @Value("${kafka.employee.event.topic}")
    private String eventTopic;

    Logger logger = Logger.getLogger(ApplicationConfiguration.class.getName());



    public ApplicationConfiguration () {

    }



    @Bean(name="kafkaProducerProperties")
    public Properties getKafkaProducerProperties() throws IOException {
        return PropertiesLoaderUtils.loadProperties(new ClassPathResource("/kafkaProducer.properties"));
    }

    @Bean(name="kafkaConsumerProperties")
    public Properties getKafkaConsumerProperties() throws IOException {
        return PropertiesLoaderUtils.loadProperties(new ClassPathResource("/kafkaConsumer.properties"));
    }

    @Bean(name="brokerProperties")
    public Properties getBrokerProperties() throws IOException {
        return PropertiesLoaderUtils.loadProperties(new ClassPathResource("/broker.properties"));
    }



    @Bean(name="kafkaConsumer")
    public KafkaConsumer<String,String> getKafkaConsumer() throws IOException {

        Properties kafkaConsumerProps = this.getKafkaConsumerProperties();

        kafkaConsumerProps.forEach((key,val) -> logger.log(Level.INFO,String.format("key-> %s val-> %s",key, val)));

        return new KafkaConsumer<>(kafkaConsumerProps);
    }

    @Bean(name="kafkaProducer")
    public KafkaProducer<String,String> getKafkaProducer() throws IOException {
        Properties properties = this.getKafkaProducerProperties();

        return new KafkaProducer<>(properties);
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
        final PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
        propertySourcesPlaceholderConfigurer.setLocation(new ClassPathResource("/application.properties"));
        return propertySourcesPlaceholderConfigurer;
    }




//    @Bean
//    public Properties kafkaProducerPropertiesBean() {
//        return getProperties("kafkaProducer.properties");
//
//    }
//
//    @Bean
//    public Properties kafkaConsumerPropertiesBean() {
//        return getProperties("kafkaConsumer.properties");
//
//    }
//
//    private Properties getProperties(String propertiesFileName) {
//        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
//        ClassPathResource classPathProperties = new ClassPathResource(propertiesFileName);
//        propertiesFactoryBean.setLocation(classPathProperties);
//
//        Properties properties = null;
//        try {
//            propertiesFactoryBean.afterPropertiesSet();
//            properties = propertiesFactoryBean.getObject();
//        }
//       catch (IOException e) {
//            //e.printStackTrace();
//           logger.log(Level.SEVERE,"Cant load properties file ${0}", classPathProperties.getFilename());
//        }
//        return properties;
//    }
}
