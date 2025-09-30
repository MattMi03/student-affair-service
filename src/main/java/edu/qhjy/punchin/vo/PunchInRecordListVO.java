package edu.qhjy.punchin.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PunchInRecordListVO {
    // 任务信息
    private String status;      // 状态: "已打卡", "未打卡"
    private LocalDate dkrq; // 打卡计划日期

    // 打卡详情 (如果已打卡)
    private LocalDateTime dksj; // 打卡时间
    private String dksb;     // 打卡设备

    // 考生信息
    private String xm; // 姓名
    private String ksh;   // 学号 (xjh)
    private String sfzjh;      // 身份证号 (sfzjh)
    private String xxmc;  // 学校
}
