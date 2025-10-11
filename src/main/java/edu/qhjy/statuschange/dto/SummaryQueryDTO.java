package edu.qhjy.statuschange.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class SummaryQueryDTO {

    private String szsdm; // 所属市代码
    private String kqdm;
    private String xxdm; // 学校代码
    private Integer jb;  // 层次


    @JsonIgnore
    private String permissionDm;

    private int pageNum = 1; // 页码
    private int pageSize = 10; // 每页条数
}
