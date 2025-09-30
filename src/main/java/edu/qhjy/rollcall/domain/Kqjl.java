package edu.qhjy.rollcall.domain;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 考勤记录实体类 (对应 kqjl 表)
 */
@Data
public class Kqjl {
    private Long kqjlbs;
    private String ksh;
    private String kqzt;
    private LocalDateTime kqsj;
    private String zqry;
}