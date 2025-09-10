package edu.qhjy.statuschange.dto;

import jakarta.validation.Valid;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import java.io.Serializable;

@Data
public class KeyPropertyChangeApplyDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 统一的学生基本信息
     */
    @NotNull
    @Valid
    private StudentBasicInfoDTO studentBasicInfoDTO;

    /**
     * 变更事项名称
     */
    private String ggsxmc;

    /**
     * 变更前的值
     */
    private String gqz;

    /**
     * 变更后的值
     */
    private String ghz;

    private String zmwjdz; // 证明文件地址
}