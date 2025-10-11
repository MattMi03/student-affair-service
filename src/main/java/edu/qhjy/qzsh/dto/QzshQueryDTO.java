package edu.qhjy.qzsh.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class QzshQueryDTO {

    int pageNum = 1;
    int pageSize = 10;
    private String xxdm;
    private Integer jb;
    private Long bjbs;
    private String ksh;
    private String xm;
    private String sfzjh;
    private String kslx;
    private String xblx;
    @JsonIgnore
    private String permissionDm; // 用于权限过滤
}
