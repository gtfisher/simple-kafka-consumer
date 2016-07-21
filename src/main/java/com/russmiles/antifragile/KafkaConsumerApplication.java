package com.russmiles.antifragile;

import com.russmiles.antifragile.api.SimpleCommandAggregate;
import org.springframework.beans.factory.annotation.Autowired;
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

		SimpleCommandAggregate aggregate = context.getBean(EmployeeCommandAggregate.class);

		aggregate.initialise();

		aggregate.run(EmployeeCommandAggregate.LOOP_FOREVER);

		//aggregate.initialiseAndRunAggregate();

	}
}
