// 文件路径: src/main/java/edu/qhjy/student/service/ClassManagerService.java
package edu.qhjy.student.service;

import com.github.pagehelper.PageInfo;
import edu.qhjy.student.dto.classmanager.ClassQueryDTO;
import edu.qhjy.student.dto.classmanager.ClassUpsertDTO;
import edu.qhjy.student.dto.classmanager.StudentAvailableQueryDTO;
import edu.qhjy.student.vo.ClassVO;
import edu.qhjy.student.vo.ImportResultVO;
import edu.qhjy.student.vo.StudentForClassVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ClassManagerService {
    PageInfo<ClassVO> listClassesByPage(ClassQueryDTO queryDTO);

    ClassVO getClassById(Long classId);

    boolean createClass(ClassUpsertDTO upsertDTO);

    boolean updateClass(ClassUpsertDTO upsertDTO, Long classId);

    boolean deleteClass(Long classId);

    PageInfo<StudentForClassVO> getStudentByClassID(Long bjbs, int pageNum, int pageSize);

    PageInfo<StudentForClassVO> getStudentAvailableByClassID(StudentAvailableQueryDTO query);

    ImportResultVO assignStudentsToClass(Long bjbs, List<String> kshList);

    /**
     * 生成分配学生的Excel模板
     */
    byte[] generateAssignStudentTemplate(String xxdm, Integer jb);

    /**
     * 通过Excel批量分配学生到班级
     *
     * @param file 上传的Excel文件
     * @return 成功分配的学生数量
     */
    ImportResultVO importStudentAssignments(String xxdm, MultipartFile file) throws IOException;

    void removeStudentsFromClass(List<String> kshList);

    String getSchoolNameByCode(String xxdm);
}
