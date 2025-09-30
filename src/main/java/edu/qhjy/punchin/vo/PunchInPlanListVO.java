package edu.qhjy.punchin.vo;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PunchInPlanListVO {
    private Long dkjhbs;
    private String xnmc;           // 学期/学年名称
    private LocalDate xqksrq;      // 学期开始日期
    private LocalDate xqjzrq;      // 学期截止日期
    private String xxmc;     // 学校名称
}