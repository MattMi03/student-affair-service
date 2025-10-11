package edu.qhjy.statuschange.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class BasicInfoQueryDTO {

    private String ksh;

    private String xm;

    private String sfzjh;

    @JsonIgnore
    private String permissionDm;

    private int pageSize = 10;

    private int pageNum = 1;
}
