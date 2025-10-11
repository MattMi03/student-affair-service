package edu.qhjy.student.dto.classmanager; // 建议放在dto/classmanager包下

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

@Data
public class AssignStudentImportData {

    @ExcelProperty("考籍号")
    @ColumnWidth(25)
    private String ksh;
}