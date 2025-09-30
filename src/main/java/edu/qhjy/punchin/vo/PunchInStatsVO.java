package edu.qhjy.punchin.vo;

import lombok.Data;

@Data
public class PunchInStatsVO {
    // 分组维度
    private String jb;      // 年级 (e.g., "2022级")
    private String xnmc;   // 学期
    private String kqmc;   // 考区
    private String xxmc; // 学校

    // 查询代码
    private String xxdm; // 学校代码

    // 统计结果
    private Long attendanceCount; // 考勤人次
    private Long absenceCount;    // 缺勤人次
    private Long leaveCount;      // 请假人次
}