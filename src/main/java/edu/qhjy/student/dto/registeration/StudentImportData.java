package edu.qhjy.student.dto.registeration;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

@Data
@ColumnWidth(20)
public class StudentImportData {

    // --- 学生基本信息 ---
    @ExcelProperty("姓名")
    private String xm;
    @ExcelProperty("身份证号")
    @ColumnWidth(25)
    private String sfzjh;
    @ExcelProperty("性别")
    private String xb;
    @ExcelProperty("民族")
    private String mz;
    @ExcelProperty("出生日期")
    private String csrq;
    @ExcelProperty("学籍号")
    private String xjh;
    @ExcelProperty("户口所在地")
    private String szxmc;
    @ExcelProperty("所在学校")
    private String xxmc;
    @ExcelProperty("班级")
    private String bjmc;
    @ExcelProperty("学籍状态")
    private String kjztmc;

    // --- 监护人信息 1 ---
    @ExcelProperty("监护人1-关系")
    private String guardian1Gx;
    @ExcelProperty("监护人1-姓名")
    private String guardian1Xm;
    @ExcelProperty("监护人1-民族")
    private String guardian1Mz;
    @ExcelProperty("监护人1-身份证号")
    @ColumnWidth(25)
    private String guardian1Sfzjh;
    @ExcelProperty("监护人1-现户籍所在地")
    private String guardian1Szxmc;
    @ExcelProperty("监护人1-工作单位")
    private String guardian1Gzdwmc;
    @ExcelProperty("监护人1-职务")
    private String guardian1Gzdwzw;
    @ExcelProperty("监护人1-联系电话")
    private String guardian1Yddh;

    // --- 监护人信息 2 ---
    @ExcelProperty("监护人2-关系")
    private String guardian2Gx;
    @ExcelProperty("监护人2-姓名")
    private String guardian2Xm;
    @ExcelProperty("监护人2-民族")
    private String guardian2Mz;
    @ExcelProperty("监护人2-身份证号")
    @ColumnWidth(25)
    private String guardian2Sfzjh;
    @ExcelProperty("监护人2-现户籍所在地")
    private String guardian2Szxmc;
    @ExcelProperty("监护人2-工作单位")
    private String guardian2Gzdwmc;
    @ExcelProperty("监护人2-职务")
    private String guardian2Gzdwzw;
    @ExcelProperty("监护人2-联系电话")
    private String guardian2Yddh;

    // --- 学历信息 1 ---
    @ExcelProperty("学历1-学历阶段")
    private String history1Xl;
    @ExcelProperty("学历1-开始日期")
    private String history1Ksrq;
    @ExcelProperty("学历1-所在学校")
    private String history1Xxmc;
    @ExcelProperty("学历1-证明人")
    private String history1Zmrxm;

    // --- 学历信息 2 ---
    @ExcelProperty("学历2-学历阶段")
    private String history2Xl;
    @ExcelProperty("学历2-开始日期")
    private String history2Ksrq;
    @ExcelProperty("学历2-所在学校")
    private String history2Xxmc;
    @ExcelProperty("学历2-证明人")
    private String history2Zmrxm;
}