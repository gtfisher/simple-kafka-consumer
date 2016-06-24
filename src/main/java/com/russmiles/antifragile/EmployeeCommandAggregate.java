package com.russmiles.antifragile;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.Consumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

import static java.util.Collections.unmodifiableList;

@Service
public class EmployeeCommandAggregate implements EventProcessor,CommandProcessor {

    private Map<EmployeeId,EmployeeDTO> employeeDTOMap = new LinkedHashMap<>();


    @Value("${kafka.employee.event.topic}")
    private String employeeEventTopic;

//    @Autowired
    private SimpleKafkaConsumer simpleKafkaConsumer;

    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public EmployeeCommandAggregate(@Qualifier("kafkaConsumer") Consumer<String,String> kafkaConsumer) {
        this.simpleKafkaConsumer = new SimpleKafkaConsumer(kafkaConsumer);
    }



    @PostConstruct
    public void inititialiseAggregate () {

        simpleKafkaConsumer.ConsumeFromTopic(Arrays.asList(employeeEventTopic),this);

    }

    @Override
    public void handleCommand (Command command) {
        Map commandParametersMap  = command.getParameters();
        switch (command.getCommandType()) {
            case "CreateEmployee" :
                EmployeeDTO employeeDTO = new EmployeeDTO(
                        (String) commandParametersMap.get("firstName"),
                        (String) commandParametersMap.get("surname"));
        }

    }


    @Override
    public void handleEvent (Event event) {
        Map eventParametersMap = event.getParameters();

        switch (event.getEventType()) {
            case "EmployeeCreated":
                //EmployeeDTO employeeDTO = (EmployeeDTO) event.getEventData();


                EmployeeDTO employeeDTO = new EmployeeDTO((EmployeeId) eventParametersMap.get("id"),
                        (String) eventParametersMap.get("firstName"), (String) eventParametersMap.get("surname"));
                employeeDTOMap.put(employeeDTO.getEmpNumber(), employeeDTO);
                break;
            case "EmployeeUpdated":
//                //EmployeeDTO employeeUpdate = (EmployeeDTO) event.getEventData();
//                EmployeeDTO employee = employeeDTOMap.get(employeeUpdate.getEmpNumber());
//                employee.setFirstName(employeeUpdate.getFirstName());
//                employee.setSurname(employeeUpdate.getSurname());
                EmployeeId employeeId = (EmployeeId) eventParametersMap.get("id");
                EmployeeDTO employee = employeeDTOMap.get(employeeId);
                employee.setFirstName((String) eventParametersMap.get("firstName"));
                employee.setSurname((String) eventParametersMap.get("surname"));
                break;
        }
    }

     public EmployeeDTO findById (EmployeeId id) {
        return employeeDTOMap.get(id);
    }

    public List<EmployeeDTO> getEmployeesList (){

        return unmodifiableList(new ArrayList<>(employeeDTOMap.values()));
    }

}

