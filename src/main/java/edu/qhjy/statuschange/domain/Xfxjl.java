package edu.qhjy.statuschange.domain;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 休复学记录实体类
 * 表名：xfxjl
 */
@Data
public class Xfxjl implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 休复学记录标识id
     */
    private Long xfxjlbs;

    /**
     * 考籍异动记录标识id
     */
    private Long kjydjlbs;

    /**
     * 休复学标记（0休学，1复学）
     */
    private Byte xfxbj;

    /**
     * 休学原因
     */
    private String xxyy;

    /**
     * 休学日期
     */
    private LocalDate xxrq;

    /**
     * 休学时长
     */
    private String xxsc;

    /**
     * 学校标识id
     */
    private String xxmc;

    /**
     * 就读年级
     */
    private String jdnj;

    /**
     * 复学学籍号
     */
    private String fxxjh;

    /**
     * 复学日期
     */
    private LocalDate fxrq;

    /**
     * 新班级标识
     */
    private String xbjmc;

    /**
     * 新就读年级
     */
    private String xjdnj;

    /**
     * 备注
     */
    private String bz;

    /**
     * 医院证明文件地址
     */
    private String yyzmwjdz;

    /**
     * 创建人姓名
     */
    private String cjrxm;

    /**
     * 创建人工作人员码
     */
    private String cjrgzrym;

    /**
     * 创建时间
     */
    private LocalDateTime cjsj;

    /**
     * 更新人姓名
     */
    private String gxrxm;

    /**
     * 更新人工作人员码
     */
    private String gxrgzrym;

    /**
     * 更新时间
     */
    private LocalDateTime gxsj;
}