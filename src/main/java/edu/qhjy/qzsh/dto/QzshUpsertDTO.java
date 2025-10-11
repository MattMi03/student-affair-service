package edu.qhjy.qzsh.dto;

import edu.qhjy.statuschange.dto.audit.AuditRequestDTO;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class QzshUpsertDTO {

    @NotNull(message = "考生标识(ksbs)不能为空")
    private Long ksbs;

    private String kslx;
    private String xblx;
    private String qt;
    private String fjdz1;
    private String fjdz2;

    private AuditRequestDTO auditRequestDTO;
}