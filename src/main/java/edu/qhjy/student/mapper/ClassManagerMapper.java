// 文件路径: src/main/java/edu/qhjy/student/mapper/ClassManagerMapper.java
package edu.qhjy.student.mapper;

import edu.qhjy.student.domain.Bjxx;
import edu.qhjy.student.dto.classmanager.BjxxDTO;
import edu.qhjy.student.dto.classmanager.ClassQueryDTO;
import edu.qhjy.student.vo.ClassVO;
import edu.qhjy.student.vo.StudentForClassVO;
import org.apache.ibatis.annotations.Mapper;

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

    List<StudentForClassVO> findAvailableStudentByClassId(Long bjbs);

    void clearStudentsFromClass(Long bjbs);

    int updateStudentBjbs(Map<String, Object> param);

    BjxxDTO getBjmcAndJbByBjbs(Long bjbs);

    Bjxx selectByXxmcAndBjmc(String xxmc, String bjmc);
}