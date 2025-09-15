// 文件路径: src/main/java/edu/qhjy/student/vo/StudentListVO.java
package edu.qhjy.student.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class StudentListVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    //    private Long ksbs;      // 学生标识ID (用于操作)
    private String xxmc;    // 学校名称
    private String ksh;     // 考籍号
    private String xm;      // 姓名
    private String sfzjh;    // 身份证号
    private Integer jb;   // 级别
    private String bjmc;    // 班级名称
    private String xb;      // 性别
    private String mz;      // 民族
    private String shzt;
    private String shyj;    // 审核意见
    private String kjztmc; // 考籍状态名称
}