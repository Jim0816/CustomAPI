package com.ljm.entity;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@ToString(callSuper = true)
@Accessors(chain = true)
public class Token implements Serializable {

    private String uid;

    private String token;

}
