// 文件路径: src/main/java/edu/qhjy/student/dto/ClassUpsertDTO.java
package edu.qhjy.student.dto.classmanager;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 新增/更新输入对象 (Upsert DTO)
 */
@Data
public class ClassUpsertDTO {
    @JsonIgnore
    private Long bjbs;

    @NotBlank(message = "班级名称不能为空")
    private String bjmc;

    @NotNull(message = "建班年级不能为空")
    @Min(value = 1900, message = "建班年级必须是四位数字")
    @Max(value = 9999, message = "建班年级必须是四位数字")
    private Integer jb;

    @NotBlank(message = "学校代码不能为空")
    private String xxdm;
//
//    @NotBlank(message = "班主任不能为空")
//    private String bzrxm;
//
//    @NotBlank(message = "班主任工作人员码不能为空")
//    private String bzrgzrym;
//
//    @NotBlank(message = "班级类型不能为空")
//    private String bjlx;
//
//    @NotBlank(message = "应试语种不能为空")
//    private String ysyz;
//
//    @NotBlank(message = "民族语种不能为空")
//    private String mzyyskyz;
}