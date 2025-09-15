package edu.qhjy.statuschange.dto;

import lombok.Data;

@Data
public class BasicInfoQueryDTO {

    private String ksh;

    private String xm;

    private String sfzjh;

    private int pageSize = 10;

    private int pageNum = 1;
}
