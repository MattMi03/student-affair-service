// 文件路径: .../statuschange/dto/ReturnApplyDTO.java
package edu.qhjy.statuschange.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Data
public class ReturnApplyDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 内嵌的学生基本信息
     */
    @NotNull
    private StudentBasicInfoDTO studentBasicInfoDTO;

    // --- 复学业务详情 ---
    /**
     * 复学日期
     */
    @NotNull(message = "复学日期不能为空")
    private LocalDate fxrq;

    /**
     * 复学后的新学籍号
     */
    private String fxxjh;

    /**
     * 复学后的新班级名称
     */
    private String xbjmc;

    /**
     * 复学后的新就读年级
     */
    private String xjdnj;

    /**
     * 备注
     */
    private String bz;

    private String yyzmwjdz; // 原因证明文件地址

    /**
     * 校验复学年级是否合法，只允许 YYYY 或 YYYY级
     * 并返回纯数字年份
     */
    public String getXjdnjYear() {
        if (xjdnj == null || xjdnj.isEmpty()) {
            throw new IllegalArgumentException("复学年级不能为空");
        }
        if (!xjdnj.matches("\\d{4}") && !xjdnj.matches("\\d{4}级")) {
            throw new IllegalArgumentException("复学年级格式不合法，只能是YYYY或YYYY级，例如2024或2024级");
        }
        // 去掉 "级" 并转成 Integer
        return xjdnj.replace("级", "");
    }

}