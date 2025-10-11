package edu.qhjy.statuschange.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class LeaveAuditListVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 考籍异动记录的唯一标识 (用于操作)
     */
    private Long kjydjlbs;

    /**
     * 考籍号
     */
    private String ksh;

    /**
     * 姓名
     */
    private String xm;

    private String xb;

    private String mz;

    /**
     * 身份证件号
     */
    private String sfzjh;

    /**
     * 级别
     */
    private Integer jb;

    /**
     * 班级
     */
    private String bjmc;

    /**
     * 休学开始时间
     */
    private LocalDate xxrq;

    /**
     * 休学时长
     */
    private String xxsc;

    /**
     * 休学原因
     */
    private String xxyy;

    /**
     * 提交时间
     */
    private LocalDateTime tjsj;

    /**
     * 审核状态
     */
    private String shzt;

    private String shjd;          // 审核节点

    private String shrxm;        // 审核人姓名

    private LocalDateTime shsj;    // 更新时间

    /**
     * 审核意见
     */
    private String shyj;

    /**
     * 学校名称
     */
    private String xxmc;

    /**
     * 就读年级
     */
    private String jdnj;

    private String zbmc;

    private String yyzmwjdz; // 医院证明文件地址

    private String kslx;       // 考生类型
}