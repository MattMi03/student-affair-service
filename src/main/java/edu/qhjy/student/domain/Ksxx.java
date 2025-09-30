package edu.qhjy.student.domain;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 考生信息表 ksxx 对应的 Java POJO
 */
@Data
public class Ksxx {
    private Long ksbs;              // 学生标识id
    private String ksh;             // 考生号
    private String xjh;             // 学籍号
    private String xm;              // 姓名
    private String xb;              // 性别 (男或女)
    private String mz;              // 民族
    private LocalDate csrq;         // 出生日期
    private String scsblhdqmc;      // 首次申报落户地省名称
    private String scsblhdsmc;      // 首次申报落户地市州名称
    private String scsblhdxmc;      // 首次申报落户地区县名称
    private String sfzjlxmc;        // 身份证件类型名称
    private String sfzjh;           // 身份证件号
    private String jtxxdzqmc;       // 家庭详细地址省名称
    private String jtxxdzsmc;       // 家庭详细地址市州名称
    private String jtxxdzxmc;       // 家庭详细地址区县名称
    private String szqmc;           // 现户籍所在省名称
    private String szsmc;           // 现户籍所在市名称
    private String szxmc;           // 现户籍所在县名称
    private String yhjszqmc;        // 原户籍所在省名称
    private String yhjszsmc;        // 原户籍所在市名称
    private String yhjszxmc;        // 原户籍所在县名称
    private LocalDate qrxzsj;       // 迁入现址时间
    private String qryy;            // 迁入原因
    private String zqzh;            // 准迁证号
    private String qyzh;            // 迁移证号
    private String zpdz;            // 照片地址
    private String zzmm;            // 政治面貌
    private String kslx;           // 考生类型
    private String yddh;            // 移动电话
    private String ysyz;            // 应试语种
    private String mzyyskyz;        // 民族语言授课语种
    private String kjztmc;          // 考籍状态
    private String kqmc;            // 考区名称
    private String xxmc;            // 学校名称
    private Integer jb;             // 届别/入学年级
    private Long bjbs;              // 班级标识id
    private String bjmc;            // 班级名称
    private Integer rxnd;           // 入学年度
    private Integer bynd;           // 毕业年度
    private String shjd;            // 审核阶段
    private String shzt;            // 审核状态
    private LocalDateTime shsj;     // 审核时间
    private String shrxm;           // 审核人姓名
    private String shyj;            // 审核意见
    private String cjrxm;           // 创建人姓名
    private String cjrgzrym;        // 创建人工作人员码
    private LocalDateTime cjsj;     // 创建时间
    private String gxrxm;           // 更新人姓名
    private String gxrgzrym;        // 更新人工作人员码
    private LocalDateTime gxsj;     // 更新时间
    private Long kjydjlbs;          // 考籍异动记录标识id (最新的一条异动记录)
    // 新增字段
    private String hklx;            // 户口类型
    // 身份证 & 户口簿扫描件
    private String ksSfzZm;         // 考生身份证正面
    private String ksSfzFm;         // 考生身份证反面
    private String fqHkbSy;         // 父亲户口簿首页
    private String fqHkbBn;         // 父亲户口簿本人页
    private String jhrHkbSy;        // 监护人户口簿首页
    private String jhrHkbBn;        // 监护人户口簿本人页
    private String ksHkbSy;         // 考生户口簿首页
    private String ksHkbBn;         // 考生户口簿本人页
    private String mqHkbSy;         // 母亲户口簿首页
    private String mqHkbBn;         // 母亲户口簿本人页
}