package com.russmiles.antifragile.configuration;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

@Configuration
public class ApplicationConfiguiration {

    Logger logger = Logger.getLogger(ApplicationConfiguiration.class.getName());

    @Bean(name="kafkaProducerProperties")
    public Properties getKafkaProducerProperties() throws IOException {
        return PropertiesLoaderUtils.loadProperties(new ClassPathResource("/kafkaProducer.properties"));
    }

    @Bean(name="kafkaConsumerProperties")
    public Properties getKafkaConsumerProperties() throws IOException {
        return PropertiesLoaderUtils.loadProperties(new ClassPathResource("/kafkaConsumer.properties"));
    }

    @Bean(name="kafkaConsumer")
    public KafkaConsumer<String,String> getKafkaConsumer() throws IOException {
        return new KafkaConsumer<>(this.getKafkaConsumerProperties());
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
