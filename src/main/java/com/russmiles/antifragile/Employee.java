package com.russmiles.antifragile;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * Created by gtarrant-fisher on 22/06/2016.
 */
public class Employee {

    private Employee() {
    }

    public static final String ID = "id";
    public static final String FIRST_NAME = "firstName";
    public static final String SURNAME = "surname";
    private String firstName;
    private String surname;
    private EmployeeId employeeID;

    public Employee(String firstName, String surname) {
        this.firstName = firstName;
        this.surname = surname;
        this.employeeID = new EmployeeId(this.hashCode());
    }

    public Employee(@JsonProperty("employeeID") EmployeeId id,
                    @JsonProperty(FIRST_NAME) String firstName,
                    @JsonProperty("surname") String surname) {
        this.employeeID = id;
        this.firstName = firstName;
        this.surname = surname;
    }

    public Employee(Map<String,Object> parametersMap) {
        this();

        EmployeeId id = new EmployeeId((Map<String, Object>) parametersMap.get("employeeID"));
        this.setEmployeeID(id);
        this.setFirstName((String) parametersMap.get(FIRST_NAME));
        this.setSurname((String) parametersMap.get(SURNAME));
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public EmployeeId getEmployeeID() {
        return employeeID;
    }


    @Override
    public String toString() {
        return "Employee{" +
                "firstName='" + firstName + '\'' +
                ", surname='" + surname + '\'' +
                ", employeeID=" + employeeID.getId() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Employee that = (Employee) o;

        if (!employeeID.equals(that.employeeID)) return false;
        if (!firstName.equals(that.firstName)) return false;
        return surname.equals(that.surname);

    }

    @Override
    public int hashCode() {
        int result = firstName.hashCode();
        result = 31 * result + surname.hashCode();
        return result;
    }

    private void setEmployeeID(EmployeeId employeeID) {
        this.employeeID = employeeID;
    }

//    public LinkedHashMap<String,Object> toMap() {
//
//        LinkedHashMap<String,Object> employeeHashMap = new LinkedHashMap<>();
//        employeeHashMap.put(ID, this.employeeID);
//        employeeHashMap.put(FIRST_NAME, this.getFirstName());
//        employeeHashMap.put("surname", this.getSurname());
//
//        return  employeeHashMap;
//
//    }
}
