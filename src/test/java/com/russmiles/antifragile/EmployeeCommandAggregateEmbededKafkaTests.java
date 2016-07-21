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
import org.junit.*;
import org.junit.runner.RunWith;
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
public class EmployeeCommandAggregateEmbededKafkaTests {




    private static ApplicationConfiguration applicationConfiguration;

    private static Log log = LogFactory.getLog(EmployeeCommandAggregateEmbededKafkaTests.class.getName());


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

    @Before
    public void setup() throws  Exception
    {
        // post some data for to queues for initialisation
        initialiseEmployeeEvents();
        Thread.sleep(2000);
    }

    private static void initialiseEmployeeEvents() throws IOException {
        SimpleKafkaProducer simpleKafkaProducer = new SimpleKafkaProducer(applicationConfiguration.getKafkaProducer(),"EmployeeCommands","EmployeeEvents");
        Employee employeeBilbo = new Employee("Bilbo","Baggins");
        Map<String,Object> map = objectMapper.convertValue(employeeBilbo, Map.class);


        simpleKafkaProducer.postToKafka(new Event("EmployeeCreated",map ) ,EMPLOYEE_EVENTS_TOPIC);

        Employee employeeFrodo = new Employee("Frodo","Baggins");
        simpleKafkaProducer.postToKafka(new Event("EmployeeCreated",objectMapper.convertValue(employeeFrodo, Map.class)),EMPLOYEE_EVENTS_TOPIC);
    }

    @AfterClass
    public static void oneTimeTearDown()
    {
        kafkaServer.shutdown();
        zkServer.shutdown();

    }


    private EmployeeCommandAggregate employeeCommandAggregate;


    @Test
    @Ignore
    public void employeeCommandAggregateInitialisationTest () throws Exception {



        employeeCommandAggregate = new EmployeeCommandAggregate(EMPLOYEE_COMMANDS_TOPIC, EMPLOYEE_EVENTS_TOPIC,
                applicationConfiguration.getKafkaConsumer(),applicationConfiguration.getKafkaProducer());

        employeeCommandAggregate.initialise();

        List<Employee> list = employeeCommandAggregate.getEmployeesList();
//        assertThat(list.size(),is(not(empty())));
        assertEquals(2,list.size());

        Employee employeeFromList = list.get(0);

        assertEquals("Bilbo",employeeFromList.getFirstName());
        assertEquals("Baggins",employeeFromList.getSurname());

        employeeFromList = list.get(1);
        assertEquals("Frodo",employeeFromList.getFirstName());
        assertEquals("Baggins",employeeFromList.getSurname());



    }

    @Test
    public void employeeCommandAggregateInitialisationAndRunTest () throws Exception {



        employeeCommandAggregate = new EmployeeCommandAggregate(EMPLOYEE_COMMANDS_TOPIC,EMPLOYEE_EVENTS_TOPIC, applicationConfiguration.getKafkaConsumer(),applicationConfiguration.getKafkaProducer());

        employeeCommandAggregate.initialise();

        List<Employee> list = employeeCommandAggregate.getEmployeesList();

        assertEquals(2,list.size());

        Employee employeeFromList = list.get(0);

        assertEquals("Bilbo",employeeFromList.getFirstName());
        assertEquals("Baggins",employeeFromList.getSurname());

        employeeFromList = list.get(1);
        assertEquals("Frodo",employeeFromList.getFirstName());
        assertEquals("Baggins",employeeFromList.getSurname());

        // aggragate is initialise, post some commands to the command topic and put into run mode

        SimpleKafkaProducer simpleKafkaProducer = new SimpleKafkaProducer(applicationConfiguration.getKafkaProducer(),"EmployeeCommands","EmployeeEvents");

        Employee employeeGandalf = new Employee("Gandalf","TheGrey");
        Command command = new Command("CreateEmployee",(Map<String,Object>) objectMapper.convertValue(employeeGandalf, Map.class));
        simpleKafkaProducer.postToKafka(command);

        employeeCommandAggregate.run(3);

        list = employeeCommandAggregate.getEmployeesList();

        assertEquals(3,list.size());


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
