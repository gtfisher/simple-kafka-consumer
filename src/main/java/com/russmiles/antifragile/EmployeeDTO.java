package com.russmiles.antifragile;

/**
 * Created by gtarrant-fisher on 22/06/2016.
 */
public class EmployeeDTO {

    private String firstName;
    private String surname;
    private EmployeeId empNumber;

    public EmployeeDTO(String firstName, String surname) {
        this.firstName = firstName;
        this.surname = surname;
        this.empNumber = new EmployeeId((long) this.hashCode());
    }

    public EmployeeDTO(EmployeeId id, String firstName, String surname) {
        this.empNumber = id;
        this.firstName = firstName;
        this.surname = surname;
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

    public EmployeeId getEmpNumber() {
        return empNumber;
    }


    @Override
    public String toString() {
        return "EmployeeDTO{" +
                "firstName='" + firstName + '\'' +
                ", surname='" + surname + '\'' +
                ", empNumber=" + empNumber +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EmployeeDTO that = (EmployeeDTO) o;

        if (empNumber != that.empNumber) return false;
        if (!firstName.equals(that.firstName)) return false;
        return surname.equals(that.surname);

    }

    @Override
    public int hashCode() {
        int result = firstName.hashCode();
        result = 31 * result + surname.hashCode();
        return result;
    }

    public void setEmpNumber(EmployeeId empNumber) {
        this.empNumber = empNumber;
    }
}
