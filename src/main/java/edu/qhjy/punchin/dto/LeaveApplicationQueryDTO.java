package edu.qhjy.punchin.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class LeaveApplicationQueryDTO {
    private String kqdm;
    private String xxdm;
    private Integer jb;
    private Long bjbs;
    private String xnmc;

    @JsonIgnore
    private String permissionDm;
}