package com.learnwebclient.service;

import com.learnwebclient.dto.Employee;
import com.learnwebclient.exception.ClientDataException;
import com.learnwebclient.exception.EmplopyeeServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.learnwebclient.constants.EmployeeConstants.*;

@Slf4j
public class EmployeeRestClient {

WebClient webClient;

    public EmployeeRestClient(WebClient webClient) {
        this.webClient = webClient;
    }



    public List<Employee> getAllEmployees(){

        return webClient.get()
                    .uri(GET_ALL_EMPLOYEE_V1)
                    .retrieve()
                    .bodyToFlux(Employee.class)
                    .collectList()
                    .block();
    }

    public Employee getEmployeeById(int employeeId) {

        try {
            return webClient.get()
                    .uri(EMPLOYEE_BY_ID_V1, employeeId)
                    .retrieve()
                    .bodyToMono(Employee.class)
                    .block();
        } catch (WebClientResponseException ex){
            log.error("Error Response code {} and Error Response Body is {}",ex.getRawStatusCode(),ex.getResponseBodyAsString());
            log.error("WebClientResponseException in getEmployeeById ",ex);
            throw ex;
        } catch (Exception ex){
            log.error("Exception in getEmployeeById ",ex);
            throw ex;
        }

    }

    public Employee getEmployeeById_custom(int employeeId) {


            return webClient.get()
                    .uri(EMPLOYEE_BY_ID_V1, employeeId)
                    .retrieve()
                    .onStatus(HttpStatus::is4xxClientError , clientResponse->handle4xxxError(clientResponse))
                    .onStatus(HttpStatus::is5xxServerError,clientResponse -> handle5xxError(clientResponse))
                    .bodyToMono(Employee.class)
                    .block();


    }

    private Mono<? extends Throwable> handle5xxError(ClientResponse clientResponse) {

        Mono<String> errorResponse = clientResponse.bodyToMono(String.class);

        return  errorResponse.flatMap((message) -> {
            log.error("ErrorResponse code is " + clientResponse.rawStatusCode() + " Error Message is : " + message);
            throw new EmplopyeeServiceException(message);
        });

    }

    private Mono<? extends Throwable> handle4xxxError(ClientResponse clientResponse) {

          Mono<String> errorResponse = clientResponse.bodyToMono(String.class);

          return  errorResponse.flatMap((message) -> {
              log.error("ErrorResponse code is " + clientResponse.rawStatusCode() + " Error Message is : " + message);
              throw new ClientDataException(message);
          });
    }


    public List<Employee> retrieveEmployeeByName(String employeeName) {

        String uri = UriComponentsBuilder.fromUriString(EMPLOYEE_BY_NAME_V1)
                .queryParam("employee_name", employeeName)
                .build().toUriString();
        try {
            return webClient.get().uri(uri)
                    .retrieve()
                    .bodyToFlux(Employee.class)
                    .collectList()
                    .block();
        } catch (WebClientResponseException ex){
            log.error("Error Response code {} and Error Response Body is {}",ex.getRawStatusCode(),ex.getResponseBodyAsString());
            log.error("WebClientResponseException in retrieveEmployeeByName ",ex);
            throw ex;
        } catch (Exception ex){
            log.error("Exception in retrieveEmployeeByName ",ex);
            throw ex;
        }
    }

    public Employee addNewEmployee(Employee employee){

        try {
            return webClient.post().uri(ADD_NEW_EMPLOYEE_V1)
                    .syncBody(employee)
                    .retrieve()
                    .bodyToMono(Employee.class)
                    .block();
        } catch (WebClientResponseException ex){
            log.error("Error Response code {} and Error Response Body is {}",ex.getRawStatusCode(),ex.getResponseBodyAsString());
            log.error("WebClientResponseException in addNewEmployee ",ex);
            throw ex;
        } catch (Exception ex){
            log.error("Exception in addNewEmployee ",ex);
            throw ex;
        }


    }

    public Employee updateEmployee(Integer employeeId,Employee employee){


        try {
            return webClient.put()
                    .uri(UPDATE_EMPLOYEE_V1, employeeId)
                    .syncBody(employee)
                    .retrieve()
                    .bodyToMono(Employee.class)
                    .block();
        }  catch (WebClientResponseException ex){
            log.error("Error Response code {} and Error Response Body is {}",ex.getRawStatusCode(),ex.getResponseBodyAsString());
            log.error("WebClientResponseException in addNewEmployee ",ex);
            throw ex;
        } catch (Exception ex){
            log.error("Exception in addNewEmployee ",ex);
            throw ex;
        }
    }


    public String deleteEmployee(Integer employeeId)
    {
        try{
            return webClient.delete().uri(EMPLOYEE_BY_ID_V1,employeeId)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (WebClientResponseException ex){
            log.error("Error Response code {} and Error Response Body is {}",
                         ex.getRawStatusCode(),ex.getResponseBodyAsString());
            log.error("WebClientResponseException in deleteEmployee ",ex);
            throw ex;
        } catch (Exception ex){
            log.error("Exception in deleteEmployee ",ex);
            throw ex;
        }
    }


    public String errorEndPoint(){

       return webClient.get().uri(ERROR_EMPLOYEE_V1)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError,clientResponse -> handle4xxxError(clientResponse))
                .onStatus(HttpStatus::is5xxServerError,clientResponse -> handle5xxError(clientResponse))
                .bodyToMono(String.class)
                .block();

    }



}
