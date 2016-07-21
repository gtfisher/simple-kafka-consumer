package com.russmiles.antifragile;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.russmiles.antifragile.api.Command;
import com.russmiles.antifragile.api.Event;
import com.russmiles.antifragile.configuration.ApplicationConfiguration;
import kafka.server.KafkaConfig;
import kafka.server.KafkaServer;
import kafka.utils.*;
import kafka.zk.EmbeddedZookeeper;
import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static java.util.Optional.empty;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApplicationConfiguration.class)
public class EmployeeCommandAggregateTests {




    private static ApplicationConfiguration applicationConfiguration;

    private static Log log = LogFactory.getLog(EmployeeCommandAggregateTests.class.getName());


    @Value("${kafka.employee.event.topic}")
    private static String employeeEventTopic;

    private static final String ZKHOST = "127.0.0.1";
    private static final String BROKERHOST = "127.0.0.1";
    private static final String BROKERPORT = "9092";
    private static final String TOPIC = "test";

    private static final String EMPLOYEE_EVENTS_TOPIC = "EmployeeEvents";
    private static final String EMPLOYEE_COMMANDS_TOPIC = "EmployeeCommands";


    private static  EmbeddedZookeeper zkServer;
    private static  KafkaServer kafkaServer;

    private static ObjectMapper objectMapper = new ObjectMapper();


    private EmployeeCommandAggregate employeeCommandAggregate;

    @BeforeClass
    public static void doOneTimeSetup () throws IOException
    {
        applicationConfiguration =  new ApplicationConfiguration();

        zkServer = new EmbeddedZookeeper();


        Properties brokerProperties =applicationConfiguration.getBrokerProperties();

        String zkConnect = brokerProperties.getProperty("zookeeper.host") + ":" + zkServer.port();
        ZkClient zkClient = new ZkClient(zkConnect, 30000, 30000, ZKStringSerializer$.MODULE$);
        ZkUtils zkUtils = ZkUtils.apply(zkClient, false);

        // setup Broker
        Properties brokerProps = new Properties();

        // empty log dir if it exists
        File logDir = new File((String) brokerProperties.get("log.dirs"));
        if (logDir.exists()) {
            deleteFolder(logDir);
        }





        brokerProperties.setProperty("zookeeper.connect", zkConnect);

        KafkaConfig config = new KafkaConfig(brokerProperties);
        Time mock = new MockTime();
        kafkaServer = TestUtils.createServer(config, mock);


    }

    @AfterClass
    public static void oneTimeTearDown()
    {
        kafkaServer.shutdown();
        zkServer.shutdown();

    }


    @Test
    public void employeeCreatedEventTest  ()  throws IOException {

        employeeCommandAggregate = new EmployeeCommandAggregate(EMPLOYEE_COMMANDS_TOPIC, EMPLOYEE_EVENTS_TOPIC, applicationConfiguration.getKafkaConsumer(),applicationConfiguration.getKafkaProducer());

        Employee employee = new Employee("Fred", "Smith");
        Map<String, Object> map = objectMapper.convertValue(employee, Map.class);

        Event event = new Event("EmployeeCreated", map);



        employeeCommandAggregate.handleEvent(event);

        List<Employee> list = employeeCommandAggregate.getEmployeesList();
        assertThat(list,is(not(empty())));
        Employee employeeFromList = list.get(0);

        assertEquals(employee.getFirstName(),employeeFromList.getFirstName());
        assertEquals(employee.getSurname(),employeeFromList.getSurname());
        assertEquals(employee.getEmployeeID().getId(),employeeFromList.getEmployeeID().getId());



//        list.stream().forEach(emp -> System.out.println(emp));


    }

    @Test
    public void employeeUpdatedEventTest ()  throws IOException {

        employeeCommandAggregate = new EmployeeCommandAggregate(EMPLOYEE_COMMANDS_TOPIC,EMPLOYEE_EVENTS_TOPIC, applicationConfiguration.getKafkaConsumer(), applicationConfiguration.getKafkaProducer());

        Employee employee = new Employee("John", "Smith");
        Map<String, Object> map = objectMapper.convertValue(employee, Map.class);

        Event event = new Event("EmployeeCreated", map);



        employeeCommandAggregate.handleEvent(event);

        List<Employee> list = employeeCommandAggregate.getEmployeesList();
        assertThat(list,is(not(empty())));
        Employee employeeFromList = list.get(0);

        assertEquals(employee.getFirstName(),employeeFromList.getFirstName());
        assertEquals(employee.getSurname(),employeeFromList.getSurname());
        assertEquals(employee.getEmployeeID().getId(),employeeFromList.getEmployeeID().getId());

        map.put("surname","Smithers");


        Event updateEvent = new Event ("EmployeeUpdated", map);

        employeeCommandAggregate.handleEvent(updateEvent);
        Employee updatedEmployeeFromList = list.get(0);

        assertEquals(employee.getFirstName(),updatedEmployeeFromList.getFirstName());
        assertEquals("Smithers",updatedEmployeeFromList.getSurname());
        assertEquals(employee.getEmployeeID().getId(),updatedEmployeeFromList.getEmployeeID().getId());



//        list.stream().forEach(emp -> System.out.println(emp));


    }


    @Test
    public void createEmployeeCommandTest () throws IOException {


        employeeCommandAggregate = new EmployeeCommandAggregate(EMPLOYEE_COMMANDS_TOPIC,EMPLOYEE_EVENTS_TOPIC,  applicationConfiguration.getKafkaConsumer(), applicationConfiguration.getKafkaProducer());


        Map<String,Object> commandMap = new LinkedHashMap<>();
        commandMap.put("firstName","Bilbo");
        commandMap.put("surname", "Baggins");
        Command command = new Command("CreateEmployee", commandMap);


        employeeCommandAggregate.handleCommand(command);

        List<Employee> list =   employeeCommandAggregate.getEmployeesList();

        assertThat(list,is(not(empty())));
        Employee updatedEmployeeFromList = list.get(0);

        assertEquals("Bilbo",updatedEmployeeFromList.getFirstName());
        assertEquals("Baggins",updatedEmployeeFromList.getSurname());



    }

    public static void deleteFolder(File folder) {

        File[] files = folder.listFiles();

        if(files!=null) {
            for (File file: files) {
                if (file.isDirectory()) {
                    deleteFolder(file);
                }
                else
                {
                    file.delete();
                }
            }
        }
        folder.delete();

    }



}
