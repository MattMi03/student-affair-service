package edu.qhjy.student.domain;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 班级信息表实体
 */
@Data
public class Bjxx {

    /**
     * 班级标识id
     */
    private Long bjbs;

    /**
     * 班级名称
     */
    private String bjmc;

    /**
     * 学校标识id
     */
    private String xxdm;

    /**
     * 级别（如2024级）
     */
    private Integer jb;

    /**
     * 班主任工作人员码
     */
    private String bzrgzrym;

    /**
     * 班主任姓名
     */
    private String bzrxm;

    /**
     * 班级类型(普通高中学校、中职综合高中试点班)
     */
    private String bjlx;

    /**
     * 应试语种
     */
    private String ysyz;

    /**
     * 民族语言授课语种
     */
    private String mzyyskyz;

    /**
     * 审核阶段
     */
    private String shjd;

    /**
     * 审核状态
     */
    private String shzt;

    /**
     * 审核时间
     */
    private LocalDateTime shsj;

    /**
     * 审核人姓名
     */
    private String shrxm;

    /**
     * 审核意见
     */
    private String shyj;

    /**
     * 更新人姓名
     */
    private String gxrxm;

    /**
     * 更新人工作人员码
     */
    private String gxrgzrym;

    /**
     * 创建时间
     */
    private LocalDateTime cjsj;

    /**
     * 创建人姓名
     */
    private String cjrxm;

    /**
     * 创建人工作人员码
     */
    private String cjrgzrym;

    /**
     * 更新时间
     */
    private LocalDateTime gxsj;
}