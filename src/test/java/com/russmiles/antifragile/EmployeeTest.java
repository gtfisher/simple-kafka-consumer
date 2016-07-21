package com.russmiles.antifragile;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;


public class EmployeeTest {


    @Test
    public void employeeToFromMapTest () {

        Employee employeeBilbo = new Employee("Bilbo","Baggins");

        ObjectMapper mapper = new ObjectMapper();

        Map<String,Object> map = mapper.convertValue(employeeBilbo,Map.class);


        Employee employeeFromMap = new Employee(map);

        assertEquals(employeeBilbo.getFirstName(), employeeFromMap.getFirstName());
        assertEquals(employeeBilbo.getSurname(), employeeFromMap.getSurname());
        assertEquals(employeeBilbo.getEmployeeID(), employeeFromMap.getEmployeeID());
        assertEquals(employeeBilbo,employeeFromMap);



    }


}
