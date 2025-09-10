package edu.qhjy.student.domain;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 学历信息表 xlxx 对应的 Java POJO
 */
@Data
public class Xlxx {
    private Long xlxxbs;          // 学历信息标识id
    private String ksh;           // 考生号
    private String xl;            // 学历
    private LocalDate ksrq;       // 开始日期
    private LocalDate jzrq;       // 截止日期
    private String xxmc;          // 学校名称
    private String zmrxm;         // 证明人姓名
    private String zmrzw;         // 证明人职位
    private String zmrdw;         // 证明人单位
    private String cjrxm;         // 创建人姓名
    private String cjrgzrym;      // 创建人工作人员码
    private LocalDateTime cjsj;   // 创建时间
    private String gxrxm;         // 更新人姓名
    private String gxrgzrym;      // 更新人工作人员码
    private LocalDateTime gxsj;   // 更新时间
}