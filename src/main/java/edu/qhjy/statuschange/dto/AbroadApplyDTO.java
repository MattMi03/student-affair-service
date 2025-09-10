// 文件路径: .../statuschange/dto/AbroadApplyDTO.java
package edu.qhjy.statuschange.dto;

import jakarta.validation.Valid;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class AbroadApplyDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 内嵌的学生基本信息
     */
    @NotNull
    @Valid
    private StudentBasicInfoDTO studentBasicInfoDTO;

    private String xxmc;

    /**
     * 出国国家
     */
    private String cggj;

    /**
     * 处理人
     */
    private String clrxm;

    /**
     * 登记时间
     */
    private LocalDate djsj;

    /**
     * 出国原因
     */
    private String cgyy;

    /**
     * 备注
     */
    private String bz;
}