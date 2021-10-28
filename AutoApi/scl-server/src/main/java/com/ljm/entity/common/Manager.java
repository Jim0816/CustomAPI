package com.ljm.entity.common;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

@Data
public class Manager {
    @Value("${manager.username}")
    private static String managerUserName;

    @Value("${manager.password}")
    private static String managerPassword;
}
