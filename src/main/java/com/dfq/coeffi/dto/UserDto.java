package com.dfq.coeffi.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserDto {

    private int id;

    private String name;

    private String refName;

    private String token;

    private String email;
    private long employeeId;
    private  String password;
    private String newPassword;
}
