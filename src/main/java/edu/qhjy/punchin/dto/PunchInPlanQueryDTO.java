package edu.qhjy.punchin.dto;

import lombok.Data;

@Data
public class PunchInPlanQueryDTO {
    private String kqdm;     // 考区代码 (用于关联查询)
    private String xxdm;   // 学校代码
    private String xnmc; // 学期名称 (对应新表的XNMC字段)

    private String permissionDm;
}