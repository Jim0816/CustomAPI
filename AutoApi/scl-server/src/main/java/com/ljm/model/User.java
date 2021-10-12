package com.ljm.model;

import com.sun.istack.internal.NotNull;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class User {

    @Id
    private String id;

    private String userId;

    private String email;

    private String name;

    private String gender;

    public User(){}

}
