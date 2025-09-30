package edu.qhjy.rollcall.service;

import com.github.pagehelper.PageInfo;
import edu.qhjy.rollcall.dto.RollCallImportDTO;
import edu.qhjy.rollcall.dto.StudentRollCallExcelQueryDTO;
import edu.qhjy.rollcall.dto.StudentRollCallQueryDTO;
import edu.qhjy.rollcall.vo.StudentRollCallListVO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface IStudentRollCallService {
    PageInfo<StudentRollCallListVO> listStudents(StudentRollCallQueryDTO query);

    void downloadStudentList(StudentRollCallExcelQueryDTO query, HttpServletResponse response);

    Map<String, Object> importRollCall(List<RollCallImportDTO> importList);


    /**
     * 从Excel文件启动导入流程
     * @param file 上传的Excel文件
     * @return 导入结果
     * @throws IOException 文件读取异常
     */
    Map<String, Object> importRollCallFromExcel(MultipartFile file) throws IOException;

    /**
     * 处理从Excel解析出的数据列表
     * @param data 从Excel读取的数据
     * @return 处理结果
     */
    Map<String, Object> processExcelData(List<StudentRollCallListVO> data);
}