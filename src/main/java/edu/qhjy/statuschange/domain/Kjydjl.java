package edu.qhjy.statuschange.domain;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 考籍异动记录实体类
 * 表名：kjydjl
 */
@Data
public class Kjydjl implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 考籍异动记录标识id
     */
    private Long kjydjlbs;

    /**
     * 考生号
     */
    private String ksh;

    /**
     * 考籍异动类型标识id
     */
    private Long kjydlxbs;

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

    /**
     * 姓名
     */
    private String xm;

    /**
     * 性别
     */
    private String xb;

    /**
     * 民族
     */
    private String mz;

    /**
     * 身份证号
     */
    private String sfzjh;

    /**
     * 招办名称
     */
    private String zbmc;
}