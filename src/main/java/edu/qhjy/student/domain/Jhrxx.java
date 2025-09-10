package edu.qhjy.student.domain;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 监护人信息表 jhrxx 对应的 Java POJO
 */
@Data
public class Jhrxx {
    private Long jhrxxbs;         // 监护人信息标识id
    private String ksh;           // 考生号
    private String gx;            // 关系
    private String xm;            // 姓名
    private String sfzjlxmc;      // 身份证件类型名称(居民身份证、护照)
    private String sfzjh;         // 身份证件号
    private String szqmc;         // 现户籍所在省名称
    private String szsmc;         // 现户籍所在市名称
    private String szxmc;         // 现户籍所在县名称
    private String gzdwmc;        // 工作单位名称
    private String gzdwzw;        // 工作单位职务
    private String mz;            // 民族
    private String yddh;          // 移动电话
    private String cjrxm;         // 创建人姓名
    private String cjrgzrym;      // 创建人工作人员码
    private LocalDateTime cjsj;   // 创建时间
    private String gxrxm;         // 更新人姓名
    private String gxrgzrym;      // 更新人工作人员码
    private LocalDateTime gxsj;   // 更新时间
}