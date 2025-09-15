package edu.qhjy.statuschange.vo;

import lombok.Data;

import java.util.Date;

/**
 * 学生个人考籍异动列表 VO
 */
@Data
public class StatusChangeVO {

    /**
     * 班级
     */
    private String className;

    /**
     * 姓名
     */
    private String studentName;

    /**
     * 操作者
     */
    private String operator;

    /**
     * 异动项目 (例如：休学、复学、调班)
     */
    private String changeItem;

    /**
     * 异动信息 (详细描述)
     */
    private String changeInfo;

    /**
     * 异动时间
     */
    private Date changeTime;

    /**
     * 证明材料 (文件URL或描述)
     */
    private String proofMaterial;
}