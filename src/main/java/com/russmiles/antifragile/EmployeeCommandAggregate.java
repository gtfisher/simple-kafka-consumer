package com.russmiles.antifragile;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.russmiles.antifragile.api.Command;
import com.russmiles.antifragile.api.Event;
import com.russmiles.antifragile.api.SimpleCommandAggregate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.producer.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

import static java.util.Collections.unmodifiableList;

@Service
public class EmployeeCommandAggregate implements SimpleCommandAggregate {

    public static final int POLL_TIMEOUT_PERIOD = 5000;
    public static final int LOOP_FOREVER = -1;

    private Map<EmployeeId,Employee> employeeMap = new LinkedHashMap<>();

    private Log log = LogFactory.getLog(getClass());




    private String employeeEventTopic;

    @Value("${kafka.employee.command.topic}")
    private String employeeCommandTopic;

    private SimpleKafkaConsumer simpleKafkaConsumer;

    private SimpleKafkaProducer simpleKafkaProducer;

    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public EmployeeCommandAggregate( @Value("${kafka.employee.command.topic}") String  employeeCommandTopic,
                                     @Value("${kafka.employee.event.topic}") String  employeeEventTopic,
            @Qualifier("kafkaConsumer") Consumer<String,String> kafkaConsumer,
                                    @Qualifier("kafkaProducer") Producer<String,String> kafkaProducer) {

        this.employeeCommandTopic = employeeCommandTopic;
        this.employeeEventTopic  = employeeEventTopic;

        this.simpleKafkaConsumer = new SimpleKafkaConsumer(kafkaConsumer);
        this.simpleKafkaProducer = new SimpleKafkaProducer(kafkaProducer, employeeCommandTopic, employeeEventTopic);
    }

    public void initialise () {
        //TODO: is a seek to beginning required here
        // initialise by polling for events from the employeeEventTopic, one this returns then all the
        // events from the topis should be consumed
        log.info("initialise");
        simpleKafkaConsumer.consumeFromEventTopic(Arrays.asList(employeeEventTopic),this, POLL_TIMEOUT_PERIOD);


    }


    // if count is set negative, should run forever
    public void run (int loopCount) {

        log.info("run");
        int count = loopCount;
        while (count > 0 || count < 0)
        {
            log.info("main loop count->" + count);
            if (count >0)
                count--;
            simpleKafkaConsumer.consumeFromCommandTopic(Arrays.asList(employeeCommandTopic),this,POLL_TIMEOUT_PERIOD);
            // delay before polling again?
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                log.error("Interrupted Exception",e);
                e.printStackTrace();
            }

        }
        log.info("main loop exit");

    }


    public void initialiseAndRunAggregate() {

        initialise();
        run(-1);

    }


    @Override
    public void handleCommand (Command command) {
        Map commandParametersMap  = command.getParameters();
        switch (command.getCommandType()) {
            case "CreateEmployee" :
                log.info("handle CreateEmployee command");
                Employee employee = new Employee(
                        (String) commandParametersMap.get("firstName"),
                        (String) commandParametersMap.get("surname"));
                log.info(String.format("employeeMap-size->%s", employeeMap.size()));

                employeeMap.put(employee.getEmployeeID(), employee);

                log.info(String.format("employeeMap-size->%s", employeeMap.size()));

                Map<String,Object> employeeMap = objectMapper.convertValue(employee,Map.class);
                Event employeeCreatedEvent = new Event("employeeCreated", employeeMap);
                log.info(String.format("Post employeeCreatedEvent %s", employee.toString()));
                log.info("Command Handled");
                simpleKafkaProducer.postToKafka(employeeCreatedEvent,employeeEventTopic);
                break;
            case "UpdateEmployee" :
                log.info("handle UpdateEmployee command");
                Employee employeetoUpdate = (Employee) this.employeeMap.get(commandParametersMap.get("id"));
                employeetoUpdate.setFirstName((String)commandParametersMap.get("firstName"));
                employeetoUpdate.setSurname((String)commandParametersMap.get("surname"));
                Event employeeUpdatedEvent = new Event("employeeUpdated", objectMapper.convertValue(employeetoUpdate,Map.class));
                log.info(String.format("Post employeeCreatedEvent %s", employeetoUpdate.toString()));
                simpleKafkaProducer.postToKafka(employeeUpdatedEvent,employeeEventTopic);
                break;

        }

    }


    @Override
    public void handleEvent (Event event) {
        Map eventParametersMap = event.getParameters();

        switch (event.getEventType()) {
            case "EmployeeCreated":

                Employee employee = new Employee(eventParametersMap);

                employeeMap.put(employee.getEmployeeID(), employee);
                break;
            case "EmployeeUpdated":
                EmployeeId employeeId = new EmployeeId((Map<String, Object>) eventParametersMap.get("employeeID"));

                Employee employeeForUpdate = employeeMap.get(employeeId);
                employeeForUpdate.setFirstName((String) eventParametersMap.get("firstName"));
                employeeForUpdate.setSurname((String) eventParametersMap.get("surname"));
                break;
        }
    }

     public Employee findById (EmployeeId id) {
        return employeeMap.get(id);
    }

    public List<Employee> getEmployeesList (){

        return unmodifiableList(new ArrayList<>(employeeMap.values()));
    }

}

