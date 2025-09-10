package edu.qhjy.student.dto.registeration;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDate;

@Data
public class StudentInfoDTO {

    // --- Fields from '基本信息' (Basic Information) section ---
    private String xm;            // 姓名
    private String xb;            // 性别
    private String mz;            // 民族
    private LocalDate csrq;       // 出生日期
    private String ksh;           // 考籍号
    private String xjh;           // 全国学籍号
    private String xxmc;          // 现就读高级中学
    private String bjmc;          // 现就读班级
    private Long bjbs;         // 现就读班级标识
    private String sfzjlxmc;      // 身份证件类型名称
    private String sfzjh;         // 身份证号
    private String zpdz;          // 半身小二寸白底彩色照片 (照片地址)
    private String yddh;          // 移动电话

    // 首次申报落户地
    private String scsblhdqmc;    // 首次申报落户地省名称
    private String scsblhdsmc;    // 首次申报落户地地市州名称
    private String scsblhdxmc;    // 首次申报落户地区县名称

    // 家庭详细地址
    private String jtxxdzqmc;     // 家庭详细地址省名称
    private String jtxxdzsmc;     // 家庭详细地址市州名称
    private String jtxxdzxmc;     // 家庭详细地址区县名称

    // 户籍信息
    private String szqmc;         // 现户口所在地 (省)
    private String szsmc;         // 现户口所在地 (市/州)
    private String szxmc;         // 现户口所在地 (县/区)
    private String yhjszqmc;      // 原户口所在地 (省)
    private String yhjszsmc;      // 原户口所在地 (市/州)
    private String yhjszxmc;      // 原户口所在地 (县/区)
    private LocalDate qrxzsj;     // 迁入现址时间

    @JsonIgnore
    private Long kjydjlbs;    // 考籍异动记录标识id (最新的一条异动记录)
}