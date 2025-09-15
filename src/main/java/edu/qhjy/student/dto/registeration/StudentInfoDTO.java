package edu.qhjy.student.dto.registeration;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDate;

@Data
public class StudentInfoDTO {

    // --- Fields from '基本信息' (Basic Information) section ---
    private String xm;            // 姓名 1
    private String xb;            // 性别 1
    private String mz;            // 民族 1
    private LocalDate csrq;       // 出生日期 1
    private String ksh;           // 考籍号 1
    private String xjh;           // 全国学籍号 1
    private String xxmc;          // 现就读高级中学 1
    private String bjmc;          // 现就读班级 1
    private String sfzjh;         // 身份证号 1
    private String zpdz;          // 半身小二寸白底彩色照片 (照片地址) 1
    private String kjztmc;      // 学籍状态 1
    private String zzmm;        // 政治面貌 1
    private Integer jb;      // 年级 1
    private String rxlb;     // 入学类别 1
    //    private String yddh;          // 移动电话
    //    private String sfzjlxmc;      // 身份证件类型名称
    private Long bjbs;         // 现就读班级标识

//    // 首次申报落户地
//    private String scsblhdqmc;    // 首次申报落户地省名称
//    private String scsblhdsmc;    // 首次申报落户地地市州名称
//    private String scsblhdxmc;    // 首次申报落户地区县名称
//
//    // 家庭详细地址
//    private String jtxxdzqmc;     // 家庭详细地址省名称
//    private String jtxxdzsmc;     // 家庭详细地址市州名称
//    private String jtxxdzxmc;     // 家庭详细地址区县名称

    // 户籍信息
    private String szqmc;         // 现户口所在地 (省)
    private String szsmc;         // 现户口所在地 (市/州)
    private String szxmc;         // 现户口所在地 (县/区)
    //    private String yhjszqmc;      // 原户口所在地 (省)
//    private String yhjszsmc;      // 原户口所在地 (市/州)
//    private String yhjszxmc;      // 原户口所在地 (县/区)
    private String hklx;                // 户口类型（城镇/农村）
    private LocalDate qrxzsj;     // 迁入现址时间


    // 考生身份证正面
    private String ksSfzZm;

    // 考生身份证反面
    private String ksSfzFm;

    // 父亲户口簿首页
    private String fqHkbSy;

    // 父亲户口簿本人页
    private String fqHkbBn;

    // 监护人户口簿首页
    private String jhrHkbSy;

    // 监护人户口簿本人页
    private String jhrHkbBn;

    // 考生户口簿首页
    private String ksHkbSy;

    // 考生户口簿本人页
    private String ksHkbBn;

    // 母亲户口簿首页
    private String mqHkbSy;

    // 母亲户口簿本人页
    private String mqHkbBn;

    @JsonIgnore
    private Long kjydjlbs;    // 考籍异动记录标识id (最新的一条异动记录)
}