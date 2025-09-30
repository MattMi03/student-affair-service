package edu.qhjy.punchin.dto;

import lombok.Data;

@Data
public class PunchInStatsQueryDTO {
    private String kqdm;     // 考区代码
    private String xxdm;     // 学校代码
    private String jb;       // 年级
    private Long bjbs;     // 班级ID
    private String xnmc;     // 学期名称
}