package edu.qhjy.statuschange.dto.remoteclass;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class AssignStudentsDTO {
    @NotEmpty
    private List<Long> ksbsList;
}