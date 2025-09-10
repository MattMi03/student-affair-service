// 文件路径: src/main/java/edu/qhjy/student/mapper/ClassManagerMapper.java
package edu.qhjy.student.mapper;

import edu.qhjy.student.domain.Bjxx;
import edu.qhjy.student.dto.classmanager.ClassQueryDTO;
import edu.qhjy.student.vo.ClassVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ClassManagerMapper {
    List<ClassVO> selectList(ClassQueryDTO queryDTO);

    Bjxx selectById(Long id);

    int insert(Bjxx bjxx);

    int updateById(Bjxx bjxx);

    int deleteById(Long id);
}