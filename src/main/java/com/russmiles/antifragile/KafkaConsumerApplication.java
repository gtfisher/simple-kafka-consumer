package com.russmiles.antifragile;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class KafkaConsumerApplication {


	public static void main(String[] args) {

        ConfigurableApplicationContext context
                = new SpringApplicationBuilder(KafkaConsumerApplication.class)
                .web(false)
                .run(args);

	}
}
