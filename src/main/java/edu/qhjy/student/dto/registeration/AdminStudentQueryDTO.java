package edu.qhjy.student.dto.registeration;

import lombok.Data;

@Data
public class AdminStudentQueryDTO {

    // --- 级联下拉框查询条件 (使用代码) ---
    private String szsdm;   // 所在市代码
    private String kqdm;    // 考区代码
    private String xxdm;    // 学校代码
    private Long bjbs;    // 班级标识ID
    private Integer jb;

    // --- 文本框查询条件 ---
    private String ksh;     // 考生号
    private String sfzjh;    // 身份证号
    private String xm;      // 姓名

    // --- 状态下拉框 ---
    private String shzt;  // 考籍状态名称

    // --- 分页参数 ---
    private int pageNum = 1;
    private int pageSize = 10;
}