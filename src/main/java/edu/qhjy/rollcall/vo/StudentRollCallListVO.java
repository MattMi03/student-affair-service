package edu.qhjy.rollcall.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat; // <-- 1. Import this annotation
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class StudentRollCallListVO {

    @ExcelProperty("学校名称")
    private String xxmc;

    @ExcelProperty("姓名")
    private String xm;

    @ExcelProperty("考号")
    private String ksh;

    @ExcelProperty("身份证号")
    private String sfzjh;

    @ExcelProperty("年级")
    private Integer jb;

    @ExcelProperty("班级")
    private String bjmc;

    @ExcelProperty("考勤状态")
    private String kqzt;

    /**
     * 2. Add @DateTimeFormat annotation here
     * This pattern must exactly match the format in your Excel file.
     */
    @ExcelProperty("考勤时间")
    private LocalDateTime kqsj;

    @ExcelProperty("专勤人员")
    private String zqry;
}