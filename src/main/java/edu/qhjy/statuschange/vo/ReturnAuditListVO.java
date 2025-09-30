// 文件路径: .../statuschange/vo/ReturnAuditListVO.java
package edu.qhjy.statuschange.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ReturnAuditListVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long kjydjlbs;      // 异动记录ID
    private String ksh;         // 考籍号
    private String xm;          // 姓名
    private String sfzjh;       // 身份证件号
    private String mz;          // 民族
    private String xb;          // 性别
    private String zbmc;        // 招办名称
    private String fxlx;        // 复学类型 (可以硬编码或从字典表获取)
    private String xxyy;        // 休学原因 (可以硬编码或从字典表获取)
    private LocalDate xxrq; // 休学日期
    private LocalDate fxrq; // 复学日期
    private String xxsc;          // 时长
    private String fxxjh;      // 复学学籍号
    private String xxmc;
    private String xbjmc;         // 新班级
    private String jdnj;        // 就读年级
    private String xjdnj;        // 新就读年级
    private String yyzmwjdz;
    private String bz;          // 备注
    private LocalDateTime cjsj; // 操作时间
    private String shjd;          // 审核节点
    private String shzt;        // 审核状态
    private String shyj;        // 审核意见

}