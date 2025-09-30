package edu.qhjy.rollcall.mapper;

import edu.qhjy.rollcall.domain.Kqjl;
import edu.qhjy.rollcall.dto.StudentRollCallExcelQueryDTO;
import edu.qhjy.rollcall.dto.StudentRollCallQueryDTO;
import edu.qhjy.rollcall.vo.StudentRollCallListVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StudentRollCallMapper {
    /**
     * 根据多条件查询学生信息
     */
    List<StudentRollCallListVO> findForPage(StudentRollCallQueryDTO query);


    /**
     * 根据多条件查询学生信息（导出Excel用）
     */
    List<StudentRollCallListVO> findForPageForExcel(StudentRollCallExcelQueryDTO query);

    // Mapper 接口
    List<String> findExistingKshs(@Param("list") List<String> kshList);

    // 批量插入并去重当天重复记录
    void batchInsertIgnoreDuplicates(@Param("list") List<Kqjl> records);

}