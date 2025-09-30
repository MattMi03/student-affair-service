package edu.qhjy.punchin.domain;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Dkjh {
    private Long dkjhbs;
    private String xnmc; // 学期名称
    private LocalDate xqksrq;
    private LocalDate xqjzrq;
    private String xxdm;
    private String shzt;
}