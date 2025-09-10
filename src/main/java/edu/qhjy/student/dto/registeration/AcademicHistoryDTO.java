package edu.qhjy.student.dto.registeration;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AcademicHistoryDTO {
    private String xl;            // 学历 (e.g., "小学", "初中")
    private LocalDate ksrq;       // 开始日期
    private LocalDate jzrq;       // 截止日期
    private String xxmc;          // 学校名称
    private String zmrxm;         // 证明人姓名
    private String zmrzw;         // 证明人职位
    private String zmrdw;         // 证明人单位
}