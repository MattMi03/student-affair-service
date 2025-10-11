package edu.qhjy.student.dto.registeration;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class AdminStatisticsQueryDTO {
    private String szsdm;   // 所在市代码
    private String kqdm;    // 考区代码
    private String xxdm;    // 学校代码
    private Integer jb;   // 建班年级

    @JsonIgnore
    private String permissionDm;

    // 分页
    private int pageNum = 1; // 当前页码
    private int pageSize = 10; // 每页记录数
}
