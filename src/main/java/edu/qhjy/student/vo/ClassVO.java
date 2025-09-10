package edu.qhjy.student.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class ClassVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    // --- 新增显示的关联字段 ---
    private String szsdm; // 所在市代码
    private String szsmc; // 所在市名称
    private String kqdm; // 考区代码
    private String kqmc;   // 考区名称
    private String xxmc;   // 学校名称
    private String xxdm;   // 学校标识ID

    // --- 原有班级核心字段 ---
    private Long bjbs;
    private String bjmc;
    private Integer jb;
    private String bzrxm;
    private String bjlx;
    private String ysyz;
    private String mzyyskyz;
}