// 文件路径: src/main/java/edu/qhjy/student/service/impl/ClassManagerServiceImpl.java
package edu.qhjy.student.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import edu.qhjy.student.domain.Bjxx;
import edu.qhjy.student.dto.classmanager.ClassQueryDTO;
import edu.qhjy.student.dto.classmanager.ClassUpsertDTO;
import edu.qhjy.student.mapper.ClassManagerMapper;
import edu.qhjy.student.service.ClassManagerService;
import edu.qhjy.student.vo.ClassVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClassManagerServiceImpl implements ClassManagerService {

    @Autowired
    private ClassManagerMapper classManagerMapper;

    @Override
    public PageInfo<ClassVO> listClassesByPage(ClassQueryDTO queryDTO) {
        PageHelper.startPage(queryDTO.getPageNum(), queryDTO.getPageSize());
        List<ClassVO> voList = classManagerMapper.selectList(queryDTO);
        return new PageInfo<>(voList);
    }

    @Override
    public ClassVO getClassById(Long classId) {
        Bjxx bjxx = classManagerMapper.selectById(classId);
        return convertToVO(bjxx);
    }

    @Override
    public boolean createClass(ClassUpsertDTO upsertDTO) {
        Bjxx bjxx = new Bjxx();
        BeanUtils.copyProperties(upsertDTO, bjxx);
        return classManagerMapper.insert(bjxx) > 0;
    }

    @Override
    public boolean updateClass(ClassUpsertDTO upsertDTO, Long classId) {
        Bjxx bjxx = new Bjxx();
        BeanUtils.copyProperties(upsertDTO, bjxx);
        bjxx.setBjbs(classId);
        int rows = classManagerMapper.updateById(bjxx);
        if (rows == 0) {
            throw new IllegalArgumentException("班级ID不存在: " + classId);
        }
        return true;
    }

    @Override
    public boolean deleteClass(Long classId) {
        return classManagerMapper.deleteById(classId) > 0;
    }

    private ClassVO convertToVO(Bjxx bjxx) {
        if (bjxx == null) {
            return null;
        }
        ClassVO vo = new ClassVO();
        BeanUtils.copyProperties(bjxx, vo);
        // 注意：此方法无法填充JOIN查询的字段（地市、学校等）
        // 如有需要，需再次查询关联表
        return vo;
    }
}