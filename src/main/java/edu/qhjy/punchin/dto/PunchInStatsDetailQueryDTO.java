package edu.qhjy.punchin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PunchInStatsDetailQueryDTO {
    // 筛选条件 (与统计接口一致)
    @NotBlank(message = "学校不能为空")
    private String xxdm;
    @NotBlank(message = "年级不能为空")
    private String jb;
    private Long bjbs;
    @NotBlank(message = "学期不能为空")
    private String xnmc;
    // 分页参数
    private int pageNum = 1;
    private int pageSize = 10;
}