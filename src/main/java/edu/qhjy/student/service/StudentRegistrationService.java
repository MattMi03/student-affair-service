package edu.qhjy.student.service;

import com.github.pagehelper.PageInfo;
import edu.qhjy.student.dto.registeration.AdminStatisticsQueryDTO;
import edu.qhjy.student.dto.registeration.AdminStudentQueryDTO;
import edu.qhjy.student.dto.registeration.AuditRequestDTO;
import edu.qhjy.student.dto.registeration.RegistrationInfoDTO;
import edu.qhjy.student.vo.StatisticsVO;
import edu.qhjy.student.vo.StudentListVO;

public interface StudentRegistrationService {

    // ---- 管理员方法 ----

    /**
     * 【新增】根据复杂条件分页查询学生列表 (供管理员使用)
     */
    PageInfo<StudentListVO> listStudentsByPage(AdminStudentQueryDTO queryDTO);

    /**
     * 管理员代为创建报名信息
     */
    void createRegistrationByAdmin(RegistrationInfoDTO registrationInfo);

    /**
     * 管理员更新指定考生的报名信息
     */
    void updateRegistrationByAdmin(String ksh, RegistrationInfoDTO registrationInfo);

    /**
     * 管理员删除指定考生的报名信息
     */
    void deleteRegistrationByAdmin(String ksh);

    /**
     * 审核学生注册信息
     *
     * @param ksh             考生号
     * @param auditRequestDTO 审核请求数据
     */
    void auditRegistration(String ksh, AuditRequestDTO auditRequestDTO);

    /**
     * 根据复杂条件查询统计信息 (供管理员使用)
     */
    PageInfo<StatisticsVO> statistics(AdminStatisticsQueryDTO queryDTO);

    // ---- 考生与公共方法 ----

    /**
     * 根据考生号获取完整的报名信息 (管理员和考生共用)
     */
    RegistrationInfoDTO getRegistrationInfo(String ksh);

    /**
     * 考生自己创建报名信息
     */
    void createRegistrationByStudent(String ksh, RegistrationInfoDTO registrationInfo);

    /**
     * 考生自己更新报名信息
     */
    void updateRegistrationByStudent(String ksh, RegistrationInfoDTO registrationInfo);

}