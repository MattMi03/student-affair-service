package edu.qhjy.qzsh.domain;

import lombok.Data;

/**
 * 高考资格前置审核实体类
 */
@Data
public class Qzsh {
    /**
     * 考生标识 (主键, 关联ksxx.KSBS)
     */
    private Long ksbs;

    /**
     * 考生类型
     */
    private String kslx;

    /**
     * 限报类型
     */
    private String xblx;

    /**
     * 其他描述
     */
    private String qt;

    /**
     * 附件地址1
     */
    private String fjdz1;

    /**
     * 附件地址2
     */
    private String fjdz2;

    private String shzt;

    private String shyj;

    private String shrxm;

    private String shsj;

    private String shjd;
}