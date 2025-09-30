package edu.qhjy.punchin.vo;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PunchInStatsDetailVO {
    // 学生信息
    private String ksh;
    private String xm;
    private String sfzjh;
    private String xxmc;
    private String bjmc;

    // 任务信息
    private LocalDate dkrq; // 缺勤或请假的日期
    private String status;  // "缺勤" 或 "请假"
}