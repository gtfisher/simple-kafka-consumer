package com.russmiles.antifragile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.InputStream;
import java.util.Properties;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = KafkaConsumerApplication.class)
public class KafkaConsumerApplicationTests {



	@Test
	public void contextLoads() {
	}

	@Test
	public void postToKafkaTopic() throws Exception {
		KafkaProducer<String, String> producer;
		try (InputStream props = Resources.getResource("kafkaProducer.properties").openStream()) {
			Properties properties = new Properties();
			properties.load(props);
			producer = new KafkaProducer<>(properties);
		}

		EmployeeDTO employeeDTO = new EmployeeDTO("Bilbo", "Baggins");
        ObjectMapper objectMapper = new ObjectMapper();

        String jsonEmployeeString = objectMapper.writeValueAsString(employeeDTO);

        try {


            producer.send(new ProducerRecord<String, String>("EmployeeEvents", jsonEmployeeString));
        }
        catch (Throwable throwable){
            System.out.printf("%s", throwable.getStackTrace());
        }
        finally {
            producer.close();
        }






	}

}
