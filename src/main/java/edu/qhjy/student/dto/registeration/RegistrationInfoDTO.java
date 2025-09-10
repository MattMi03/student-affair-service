package edu.qhjy.student.dto.registeration;

import lombok.Data;

import java.util.List;

/**
 * 考生注册信息 DTO (Data Transfer Object).
 * This class aggregates all the information required for a student's registration,
 * mirroring the structure of the web form. It combines data that will populate
 * the Ksxx (student), Jhrxx (guardian), and Xlxx (academic history) entities.
 */
@Data
public class RegistrationInfoDTO {

    /**
     * Corresponds to the '基本信息' (Basic Information) and '户籍信息' (Household Registration) sections.
     * This part maps primarily to the Ksxx entity.
     */
    private StudentInfoDTO studentInfo;

    /**
     * Corresponds to the '监护人信息' (Guardian Information) section.
     * This is a list because the form allows for multiple guardians (e.g., father, mother).
     * Each item in the list maps to a Jhrxx entity.
     */
    private List<GuardianInfoDTO> guardians;

    /**
     * Corresponds to the '学历信息' (Academic History) section.
     * Each item in this list maps to an Xlxx entity.
     */
    private List<AcademicHistoryDTO> academicHistories;

    /**
     * Nested DTO for Guardian Information.
     * Maps to the fields in the Jhrxx entity.
     */

    /**
     * Nested DTO for Academic History.
     * Represents a single entry in the '学历信息' (Academic History) table.
     * UPDATED to match the provided Xlxx.java POJO.
     */

}