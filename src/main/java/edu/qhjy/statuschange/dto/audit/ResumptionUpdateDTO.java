package edu.qhjy.statuschange.dto.audit;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 管理员为复学申请补充新年级、新班级信息的 DTO
 */
@Data
public class ResumptionUpdateDTO {

    @NotBlank(message = "新班级不能为空")
    private String xbjmc; // 新班级名称

    //    @NotNull(message = "新班级不能为空")
    private Long xbjbs;

    @NotBlank(message = "新年级不能为空")
    @Pattern(regexp = "\\d{4}(级)?", message = "复学年级格式不合法，只能是YYYY或YYYY级，例如2024或2024级")
    private String xjdnj; // 新就读年级


    public void setXjdnj(String xjdnj) {
        this.xjdnj = getXjdnjYear(xjdnj);
    }

    private String getXjdnjYear(String xjdnj) {
        // 去掉 "级" 并转成 Integer
        return xjdnj.replace("级", "");
    }
}