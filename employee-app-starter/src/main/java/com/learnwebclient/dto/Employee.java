package com.learnwebclient.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Employee {

    private Integer age;
    private String firstName;
    private String lastName;
    private Integer id;
    private String role;
    private String gender;

}
