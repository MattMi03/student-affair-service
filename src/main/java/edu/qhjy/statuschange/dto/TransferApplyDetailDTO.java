package edu.qhjy.statuschange.dto;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Data
public class TransferApplyDetailDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String jdnj;      // 就读年级
    private String yjdbj;    // 原就读班级
    private String yxxmc;       // 转出学校 (原学校)
    private String xxmc;        // 转入学校 (现学校)
    private String zrsfmc;      // 转入省份
    private String zrcsmc;      // 转入城市
    private String zrqxmc;      // 转入区县
    private String jdbj;      // 班级
    private LocalDate zcsj;     // 转出时间
    private String zcyy;        // 转出原因
    private String scwjdz;    // 审查文件地址

    @NotNull
    @Valid
    private StudentBasicInfoDTO studentBasicInfoDTO;

}
