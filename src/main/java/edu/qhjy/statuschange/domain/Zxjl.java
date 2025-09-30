package edu.qhjy.statuschange.domain;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 转学记录实体类
 */
@Data
public class Zxjl implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 转学记录标识id
     */
    private Long zxjlbs;

    /**
     * 考籍异动记录标识id
     */
    private Long kjydjlbs;

    private String scwjdz;


    private String jdsqzmwjdz; // 就读申请证明文件地址
    private String mbxxlqzmwjdz; // 目标学校录取证明文件地址
    private String swjdsxbawjdz; // 省外就读手续备案文件地址

    /**
     * 转出原因
     */
    private String zcyy;

    /**
     * 原所在省名称
     */
    private String yszqmc;

    /**
     * 原所在市名称
     */
    private String yszsmc;

    /**
     * 原所在县名称
     */
    private String yszxmc;

    /**
     * 原学校名称
     */
    private String yxxmc;

    /**
     * 原就读班级
     */
    private String yjdbj;

    /**
     * 原就读年级
     */
    private String yjdnj;

    /**
     * 现所在省名称
     */
    private String szqmc;

    /**
     * 现所在市名称
     */
    private String szsmc;

    /**
     * 现所在县名称
     */
    private String szxmc;

    /**
     * 现学校名称
     */
    private String xxmc;

    /**
     * 现就读年级
     */
    private String jdnj;

    private Long bjbs;

    /**
     * 就读班级
     */
    private String jdbj;

    private LocalDate zcsj;

    /**
     * 转学类型（1省内转出，2省内转入，3省外转出，4省外转入）
     */
    private Long zxlx;

    /**
     * 创建人姓名
     */
    private String cjrxm;

    /**
     * 创建人工号
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
     * 更新人工号
     */
    private String gxrgzrym;

    /**
     * 更新时间
     */
    private LocalDateTime gxsj;
}