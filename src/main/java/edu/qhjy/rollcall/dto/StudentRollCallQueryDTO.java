package edu.qhjy.rollcall.dto;

import lombok.Data;

@Data
public class StudentRollCallQueryDTO {
    // 筛选条件
    private String xxdm;
    private String xm;
    private String sfzjh;
    private Integer jb;
    private Long bjbs;

    // 分页参数
    private int pageNum = 1;
    private int pageSize = 10;
}