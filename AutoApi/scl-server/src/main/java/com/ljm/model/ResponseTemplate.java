package com.ljm.model;

import lombok.Data;

@Data
public class ResponseTemplate {

    private String url;
    private String type;//业务类型
    private String collectionName;
    private String operate;//操作类型a
    private String data;
}
