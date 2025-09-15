// 文件路径: src/main/java/edu/qhjy/student/service/ClassManagerService.java
package edu.qhjy.student.service;

import com.github.pagehelper.PageInfo;
import edu.qhjy.student.dto.classmanager.ClassQueryDTO;
import edu.qhjy.student.dto.classmanager.ClassUpsertDTO;
import edu.qhjy.student.vo.ClassVO;
import edu.qhjy.student.vo.StudentForClassVO;

import java.util.List;

public interface ClassManagerService {
    PageInfo<ClassVO> listClassesByPage(ClassQueryDTO queryDTO);

    ClassVO getClassById(Long classId);

    boolean createClass(ClassUpsertDTO upsertDTO);

    boolean updateClass(ClassUpsertDTO upsertDTO, Long classId);

    boolean deleteClass(Long classId);

    PageInfo<StudentForClassVO> getStudentByClassID(Long bjbs, int pageNum, int pageSize);

    PageInfo<StudentForClassVO> getStudentAvailableByClassID(Long classId, int pageNum, int pageSize);

    int assignStudentsToClass(Long bjbs, List<String> kshList);
}
