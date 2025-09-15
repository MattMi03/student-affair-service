// 文件路径: .../statuschange/vo/InformationChangeSummaryVO.java
package edu.qhjy.statuschange.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class InformationChangeSummaryVO implements Serializable {
    private static final long serialVersionUID = 1L;

    // --- 学生基本信息 ---
    private String ksh;
    private String xm;
    private String sfzjh;
    private Integer jb;
    private String bjmc;

    // --- 各类异动的统计次数 ---
    private int keyPropertyChangeCount; // 关键信息变更次数
    private int returnSchoolCount;      // 复学次数
    private int leaveSchoolCount;       // 休学次数
    private int inProvinceInCount;      // 省内转入次数
    private int inProvinceOutCount;     // 省内转出次数
    private int outOfProvinceInCount;   // 省外转入次数
    private int outOfProvinceOutCount;  // 转出省外次数
}