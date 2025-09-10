package edu.qhjy.statuschange.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TransferAuditListVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Long kjydjlbs;      // 异动记录ID
    private String ksh;         // 考籍号
    private String xm;          // 姓名
    private String sfzjh;       // 身份证号
    private Integer jb;       // 级别
    private String jdnj;      // 就读年级
    private String yjdbj;    // 原就读班级 (假设是原班级)
    private String yxxmc;      // 原转出学校 (原学校)
    private String xxmc;        // 转入学校 (现学校)
    private String zrsfmc;   // 转入省份
    private String zrcsmc;   // 转入城市
    private String zrqxmc;      // 转入区县
    private String jdbj;      // 班级
    private String scwjdz;    // 审查文件地址
    private LocalDate zcsj;     // 转出时间 (假设用创建时间)
    private String zcyy;        // 转出原因 (可以从审核意见获取)
    private LocalDateTime tjsj; // 提交时间 (即申请创建时间)
    private String shzt;        // 审核状态
    private String shyj;        // 审核意见
    private String mz;
    private String xb;
    private String zbmc;
}