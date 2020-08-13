package com.learnwebclient.service;

import com.learnwebclient.dto.Employee;
import com.learnwebclient.exception.ClientDataException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.retry.RetryExhaustedException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EmployeeRestClientTest {

    private static final String baseUrl ="http://localhost:8081/employeeservice";

    WebClient webClient = WebClient.create(baseUrl);

    EmployeeRestClient employeeRestClient = new EmployeeRestClient(webClient);
    @Test
    void testToGetAllEmployees(){

        List<Employee> employeeList =  employeeRestClient.getAllEmployees();

        assert (employeeList.size()>0);

    }

    @Test
    void testToGetEmployeeById(){
        int employeeId=1;
        Employee employee = employeeRestClient.getEmployeeById(employeeId);
        assertEquals("Chris",employee.getFirstName());
    }
    @Test
    void testToGetEmployeeById_NotFound(){
        int employeeId=10;
        Assertions.assertThrows(WebClientResponseException.class,
                ()->employeeRestClient.getEmployeeById(employeeId));
    }

    @Test
    void testToGetEmployeeById_withRetry(){
        int employeeId=10;
        Assertions.assertThrows(WebClientResponseException.class,
                ()->employeeRestClient.getEmployeeById_withRetry(employeeId));
    }


    @Test
    void testToGetEmployeeById_custom(){
        int employeeId=10;

        Assertions.assertThrows(ClientDataException.class,()->employeeRestClient.getEmployeeById_custom(employeeId));

    }

    @Test
    void testRetrieveEmployeeByName(){

        String name ="Chris";

        List<Employee> employees = employeeRestClient.retrieveEmployeeByName(name);
        assertTrue(employees.size()>0);
        assertEquals("Chris",employees.get(0).getFirstName());


    }

    @Test
    void testRetrieveEmployeeByName_NotFound(){

        String name ="Sreeram";
        Assertions.assertThrows(WebClientResponseException.class,
                ()->employeeRestClient.retrieveEmployeeByName(name));


    }

    @Test
    void addNewEmployeeTest(){

        Employee employee = Employee.builder().age(42).firstName("Iron")
                            .lastName("Man")
                            .role("Lead Engineer")
                            .gender("Male")
                            .build();

       Employee returnedEmployee = employeeRestClient.addNewEmployee(employee);
       assertTrue(returnedEmployee.getId() !=null);
       assertEquals(returnedEmployee.getFirstName(),employee.getFirstName());
    }

    @Test
    void addNewEmployeeTest_BAD_REQUEST(){

        Employee employee = Employee.builder().age(42)
                .lastName("Man")
                .role("Lead Engineer")
                .gender("Male")
                .build();

      Assertions.assertThrows(WebClientResponseException.class,
              ()->employeeRestClient.addNewEmployee(employee));
    }

    @Test
    void updateEmployeeTest(){
        ;
        Employee employee =Employee.builder().age(42).firstName("Adam1")
                .lastName("Sandler1")
                .role("Lead Engineer")
                .gender("Male")
                .build();

        Employee updatedEmployee = employeeRestClient.updateEmployee(2,employee);

        assertEquals("Adam1",updatedEmployee.getFirstName());
        assertEquals("Sandler1",updatedEmployee.getLastName());
    }

    @Test
    void updateEmployeeTest_NOT_FOUND() {

        Employee employee = Employee.builder().age(42).firstName("Adam1")
                .lastName("Sandler1")
                .role("Lead Engineer")
                .gender("Male")
                .build();

        Assertions.assertThrows(WebClientResponseException.class,
                () -> employeeRestClient.updateEmployee(200, employee));

    }

    @Test
    void deleteEmployeeTest(){

        Employee employee = Employee.builder().age(42).firstName("Iron3")
                .lastName("Man3")
                .role("Lead Engineer")
                .gender("Male")
                .build();

        Employee returnedEmployee = employeeRestClient.addNewEmployee(employee);

       String response = employeeRestClient.deleteEmployee(returnedEmployee.getId());

       assertEquals("Employee deleted successfully.",response);

    }

    @Test
    void errorEndPointTest(){

        Assertions.assertThrows(RetryExhaustedException.class,()->employeeRestClient.errorEndPoint());

    }

}
