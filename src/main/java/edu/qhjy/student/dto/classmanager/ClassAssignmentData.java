package edu.qhjy.student.dto.classmanager; // 建议放在dto/classmanager包下

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

@Data
public class ClassAssignmentData {

    @ExcelProperty("考籍号")
    @ColumnWidth(25)
    private String ksh;

    @ExcelProperty("姓名")
    @ColumnWidth(15)
    private String xm;

    @ExcelProperty("身份证号")
    @ColumnWidth(25)
    private String sfzjh;

    @ExcelProperty("年级")
    @ColumnWidth(15)
    private Integer jb;

    @ExcelProperty("班级")
    @ColumnWidth(20)
    private String bjmc;

}