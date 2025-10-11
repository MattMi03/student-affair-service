package edu.qhjy.punchin.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class PunchInRecordQueryDTO {
    // 筛选条件
    private String kqdm;
    private String xxdm;
    private String jb; // 年级
    private String bjbs; // 班级ID (bjbs)
    private String xnmc; // 打卡计划ID (dkjhbs)

    @JsonIgnore
    private String permissionDm;

    // 分页参数
    private int pageNum = 1;
    private int pageSize = 10;
}
