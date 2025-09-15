package edu.qhjy.student.dto.classmanager;

import lombok.Data;

import java.util.List;

@Data
public class AssignStudentsDTO {
    private Long bjbs;
    private List<String> kshList;
}