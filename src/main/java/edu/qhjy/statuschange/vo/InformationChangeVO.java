package edu.qhjy.statuschange.vo;

import lombok.Data;

import java.util.Date;

/**
 * 学生个人信息变动列表 VO
 */
@Data
public class InformationChangeVO {

    /**
     * 变动项目 (例如：考籍号变更为...)
     */
    private String changeItem;

    /**
     * 操作人
     */
    private String operator;

    /**
     * 时间
     */
    private Date time;

    /**
     * 状态 (0: 正常, 1: 已处理等)
     */
    private Integer status;
}