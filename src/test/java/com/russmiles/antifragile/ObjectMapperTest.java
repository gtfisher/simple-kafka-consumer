package com.russmiles.antifragile;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.util.*;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.MatcherAssert.assertThat;

public class ObjectMapperTest {

    ObjectMapper objectMapper = new ObjectMapper();


    @Test
    public void employeeToJson () throws Exception {

        Employee employee = new Employee("Fred","Smith");

        String jsonEventString = objectMapper.writeValueAsString(employee);

        assertNotNull(jsonEventString);
        assertEquals("{\"employeeID\":{\"id\":148146872},\"firstName\":\"Fred\",\"surname\":\"Smith\"}",
                jsonEventString);

        Map map = objectMapper.readValue(jsonEventString,Map.class);

        Employee employee2 = new Employee(map);


        Employee employee1 = objectMapper.readValue(jsonEventString,Employee.class);

        assertEquals(employee.getFirstName(),employee1.getFirstName());
        assertEquals(employee.getSurname(),employee1.getSurname());
        assertEquals(employee.getEmployeeID(),employee1.getEmployeeID());





    }




    @Test
    public void employeeDecodeValidJson () throws Exception {

        String employeeString = "{\"employeeID\":{\"id\":148146872},\"firstName\":\"Fred\",\"surname\":\"Smith\"}";

        Map<String, Object> map = new LinkedHashMap<>();

        map = objectMapper.readValue(employeeString, Map.class);

        assertEquals(3,map.size());
        Employee employee = new Employee( (Map<String,Object>) map);

        assertNotNull(employee);
        assertEquals("Fred",employee.getFirstName());
        assertEquals("Smith", employee.getSurname());
        assertEquals(148146872,employee.getEmployeeID().getId());

    }


    @Test(expected = NullPointerException.class)
    public void employeeDecodeInvalidJson () throws Exception {

        String employeeString = "{\"employee\":{\"id\":148146872,\"first\":\"Fred\",\"surname\":\"Smith\"} }";

        Map<String, Object> map = new LinkedHashMap<>();

        map = objectMapper.readValue(employeeString, Map.class);

        //assertEquals(1,map.size());
        Employee employee = new Employee( (Map<String,Object>) map.get("employee"));

//        assertNotNull(employee);
//        assertEquals("Fred",employee.getFirstName());
//        assertEquals("Smith", employee.getSurname());
//        assertEquals(148146872,employee.getEmployeeID().getId());

    }

}
