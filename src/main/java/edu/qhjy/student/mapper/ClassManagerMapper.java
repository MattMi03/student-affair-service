// 文件路径: src/main/java/edu/qhjy/student/mapper/ClassManagerMapper.java
package edu.qhjy.student.mapper;

import edu.qhjy.student.domain.Bjxx;
import edu.qhjy.student.domain.Ksxx;
import edu.qhjy.student.dto.classmanager.BjxxDTO;
import edu.qhjy.student.dto.classmanager.ClassAssignmentData;
import edu.qhjy.student.dto.classmanager.ClassQueryDTO;
import edu.qhjy.student.dto.classmanager.StudentAvailableQueryDTO;
import edu.qhjy.student.vo.ClassVO;
import edu.qhjy.student.vo.StudentForClassVO;
import jakarta.validation.constraints.NotBlank;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ClassManagerMapper {
    List<ClassVO> selectList(ClassQueryDTO queryDTO);

    Bjxx selectById(Long id);

    int insert(Bjxx bjxx);

    int updateById(Bjxx bjxx);

    int deleteById(Long id);

    List<StudentForClassVO> findStudentByClassId(Long bjbs);

    List<StudentForClassVO> findAvailableStudentByClassId(StudentAvailableQueryDTO query);

    void clearStudentsFromClass(Long bjbs);

    int updateStudentBjbs(Map<String, Object> param);

    BjxxDTO getBjmcAndJbByBjbs(Long bjbs);

    Bjxx selectByXxmcAndBjmc(String xxmc, String bjmc);

    int findSchoolByXxdm(@NotBlank(message = "学校代码不能为空") String xxdm);

    Bjxx findByXxdmAndBjmc(@Param("xxdm") String xxdm, @Param("bjmc") String bjmc);

    List<Bjxx> findClassesBySchoolAndNameBatch(@Param("pairs") List<Map<String, String>> schoolClassPairs);

    List<Ksxx> findStudentsByKshList(List<String> kshList);

    void removeStudentsFromClass(List<String> kshList);

    List<ClassAssignmentData> findStudentsForClassAssignmentTemplate(
            @Param("xxdm") String xxdm,
            @Param("jb") Integer jb
    );

    String findSchoolNameByCode(String xxdm);
}