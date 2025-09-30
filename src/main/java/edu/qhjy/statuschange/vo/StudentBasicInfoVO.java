package edu.qhjy.statuschange.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Data
public class StudentBasicInfoVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    // 核心信息
    private String ksh;        // 考生号
    private String kjztmc; // 考籍状态名称
    private String xm;          // 姓名
    private String xb;          // 性别
    private String mz;          // 民族
    private LocalDate csrq;   // 出生日期
    private String sfzjh;       // 身份证号

    // 学籍信息
    private String xxmc;        // 就读学校
    private String bjmc;        // 班级名称
    private String jdnj;      // 就读年级
    private String zbmc;        // 招办名称

}