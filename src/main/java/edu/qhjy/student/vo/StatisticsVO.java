package edu.qhjy.student.vo;

import lombok.Data;

@Data
public class StatisticsVO {

    private Integer jb;
    private String ssxzqhmc;
    private String kqmc;
    private String xxmc;
    private Integer zxrs; // 在校人数
    private Integer xxrs; // 休学人数
    private Integer lsrs; // 流失人数
    private Integer byrs; // 毕业人数
    private Integer zrs;  // 总人数
}
