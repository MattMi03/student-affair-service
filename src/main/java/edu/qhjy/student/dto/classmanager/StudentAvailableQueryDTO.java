package edu.qhjy.student.dto.classmanager;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StudentAvailableQueryDTO {

    @NotNull(message = "班级ID不能为空")
    private Long bjbs;

    private String ksh;
    private String sfzjh;
    private String xm; // 支持模糊查询

    private int pageNum = 1;
    private int pageSize = 10;
}