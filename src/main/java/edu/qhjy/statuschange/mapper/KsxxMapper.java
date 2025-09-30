package edu.qhjy.statuschange.mapper;

import edu.qhjy.statuschange.dto.audit.StudentLocationDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 考生信息表 Mapper 接口
 */
@Mapper
public interface KsxxMapper {

    /**
     * 根据考生号，查询其所属的各级区划代码，用于权限校验
     *
     * @param ksh 考生号
     * @return 学生区划位置 DTO
     */
    StudentLocationDTO findStudentLocationByKsh(@Param("ksh") String ksh);

    /**
     * 更新学生的考籍状态 (用于休学、流失、出国、补录等)
     *
     * @param ksh    考生号
     * @param status 最新的考籍状态名称, e.g., "休学"
     * @return 更新的行数
     */
    int updateStudentStatus(@Param("ksh") String ksh, @Param("status") String status);

    /**
     * 更新学生的复学信息
     *
     * @param ksh           考生号
     * @param newClassName  新班级名称
     * @param newGradeLevel 新就读年级
     * @return 更新的行数
     */
    int updateStudentResumptionInfo(@Param("ksh") String ksh, @Param("newClassName") String newClassName, @Param("newGradeLevel") Integer newGradeLevel, @Param("newClassId") Long newClassId);

    /**
     * 更新学生的学校信息 (用于转学)
     *
     * @param ksh           考生号
     * @param newSchoolName 新学校名称
     * @param newClassName  新班级名称
     * @param newGradeLevel 新就读年级
     * @return 更新的行数
     */
    int updateStudentSchoolInfo(@Param("ksh") String ksh, @Param("newSchoolName") String newSchoolName, @Param("newClassName") String newClassName, @Param("newClassCode") Long bjbs, @Param("newGradeLevel") Integer newGradeLevel);

    /**
     * 动态更新学生的某个关键属性 (用于信息变更)
     *
     * @param ksh          考生号
     * @param propertyName 要更新的属性名 (对应数据库列名)
     * @param newValue     新的值
     * @return 更新的行数
     */
    int updateStudentKeyProperty(@Param("ksh") String ksh, @Param("propertyName") String propertyName, @Param("newValue") Object newValue);

    String findMaxKshByPrefix(String newKshPrefix);
}