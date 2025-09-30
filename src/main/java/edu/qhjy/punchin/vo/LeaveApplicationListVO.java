package edu.qhjy.punchin.vo;

import lombok.Data;

import java.time.LocalDate;

@Data
public class LeaveApplicationListVO {
    private Long qjjlbs;
    private String ksh;
    private String xm;
    private String sfzjh;
    private String xxmc;
    private LocalDate ksrq;
    private LocalDate jsrq;
    private String zmcllb;
    private String shzt;
    private String shyj;
}