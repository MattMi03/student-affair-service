// 文件路径: src/main/java/edu/qhjy/student/mapper/StudentRegistrationMapper.java
package edu.qhjy.student.mapper;

import edu.qhjy.student.domain.Jhrxx;
import edu.qhjy.student.domain.Ksxx;
import edu.qhjy.student.domain.Xlxx;
import edu.qhjy.student.dto.registeration.AdminStatisticsQueryDTO;
import edu.qhjy.student.dto.registeration.AdminStudentQueryDTO;
import edu.qhjy.student.vo.StatisticsVO;
import edu.qhjy.student.vo.StudentListVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface StudentRegistrationMapper {

    /**
     * 【新增】管理员端列表查询方法
     */
    List<StudentListVO> selectStudentList(AdminStudentQueryDTO queryDTO);

    List<StatisticsVO> selectStatistics(AdminStatisticsQueryDTO queryDTO);

    // === 考生端与公共方法 ===
    Ksxx findStudentInfoByKsh(String ksh);

    List<Jhrxx> findGuardianInfoByKsh(String ksh);

    List<Xlxx> findAcademicHistoryByKsh(String ksh);

    int existsByKsh(String ksh);

    void insertStudentInfo(Ksxx studentInfo);

    void insertGuardians(@Param("guardians") List<Jhrxx> guardians);

    void insertAcademicHistories(@Param("academicHistories") List<Xlxx> academicHistories);

    void updateStudentInfo(Ksxx studentInfo);

    void deleteGuardiansByKsh(String ksh);

    void deleteAcademicHistoriesByKsh(String ksh);

    void deleteStudentByKsh(String ksh);

    String findSchoolDmBySchoolName(String schoolName);

    List<Map<String, Object>> findSchoolDmsByNames(@Param("list") List<String> names);

    List<String> findExistingKshs(@Param("list") List<String> kshList);

    void batchInsertStudents(@Param("list") List<Ksxx> students);

    void batchInsertGuardians(@Param("list") List<Jhrxx> guardians);

    void batchInsertAcademicHistories(@Param("list") List<Xlxx> histories);

    List<String> findExistingSfzjhs(@Param("list") List<String> sfzjhList);
}