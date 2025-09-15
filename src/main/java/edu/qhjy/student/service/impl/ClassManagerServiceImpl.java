// 文件路径: src/main/java/edu/qhjy/student/service/impl/ClassManagerServiceImpl.java
package edu.qhjy.student.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import edu.qhjy.student.domain.Bjxx;
import edu.qhjy.student.dto.classmanager.BjxxDTO;
import edu.qhjy.student.dto.classmanager.ClassQueryDTO;
import edu.qhjy.student.dto.classmanager.ClassUpsertDTO;
import edu.qhjy.student.mapper.ClassManagerMapper;
import edu.qhjy.student.service.ClassManagerService;
import edu.qhjy.student.vo.ClassVO;
import edu.qhjy.student.vo.StudentForClassVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Override
    public PageInfo<StudentForClassVO> getStudentByClassID(Long bjbs, int pageNum, int pageSize) {
        // 开启分页
        PageHelper.startPage(pageNum, pageSize);

        // 查询班级学生列表
        List<StudentForClassVO> result = classManagerMapper.findStudentByClassId(bjbs);

        // 用 PageInfo 封装分页信息
        return new PageInfo<>(result);
    }

    @Override
    public PageInfo<StudentForClassVO> getStudentAvailableByClassID(Long bjbs, int pageNum, int pageSize) {
        // 开启分页
        PageHelper.startPage(pageNum, pageSize);

        // 查询班级可用学生列表（可用的学生假设用 SHZT='已审核' 或其他状态判断）
        List<StudentForClassVO> result = classManagerMapper.findAvailableStudentByClassId(bjbs);

        // 返回分页结果
        return new PageInfo<>(result);
    }

    @Override
    @Transactional  // 保证原子性
    public int assignStudentsToClass(Long bjbs, List<String> kshList) {
        if (bjbs == null || kshList == null || kshList.isEmpty()) {
            throw new IllegalArgumentException("班级ID或学生列表不能为空");
        }

        BjxxDTO bjxx = classManagerMapper.getBjmcAndJbByBjbs(bjbs);
        if (bjxx == null) {
            throw new IllegalArgumentException("班级ID不存在: " + bjbs);
        }

        String bjmc = bjxx.getBjmc();
        Integer jb = bjxx.getJb();

        classManagerMapper.clearStudentsFromClass(bjbs);

        // 3. 批量更新新的学生
        if (!kshList.isEmpty()) {
            Map<String, Object> param = new HashMap<>();
            param.put("bjbs", bjbs);
            param.put("bjmc", bjmc);
            param.put("jb", jb);
            param.put("kshList", kshList);
            return classManagerMapper.updateStudentBjbs(param);
        }
        return 0;
    }
}