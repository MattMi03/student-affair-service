package edu.qhjy.student.service.impl;

import com.alibaba.excel.EasyExcel;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import edu.qhjy.aop.UserContext;
import edu.qhjy.student.domain.Bjxx;
import edu.qhjy.student.domain.Ksxx;
import edu.qhjy.student.dto.classmanager.*;
import edu.qhjy.student.mapper.ClassManagerMapper;
import edu.qhjy.student.service.ClassManagerService;
import edu.qhjy.student.vo.ClassVO;
import edu.qhjy.student.vo.ImportResultVO;
import edu.qhjy.student.vo.StudentForClassVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ClassManagerServiceImpl implements ClassManagerService {

    @Autowired
    private ClassManagerMapper classManagerMapper;

    private void applyUserPermission(ClassQueryDTO queryDTO) {
        UserContext.UserInfo user = UserContext.get();
        if (user != null && user.getDm() != null) {
            String permissionDm = user.getDm();
            queryDTO.setPermissionDm(permissionDm);
        }
    }

    @Override
    public PageInfo<ClassVO> listClassesByPage(ClassQueryDTO queryDTO) {
        applyUserPermission(queryDTO);
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
    @Transactional
    public boolean createClass(ClassUpsertDTO upsertDTO) {
        // 校验1：学校是否存在 (您的代码中已有)
        if (classManagerMapper.findSchoolByXxdm(upsertDTO.getXxdm()) < 1) {
            throw new IllegalArgumentException("所选择的学校不存在");
        }

        // [NEW] 校验2：在该学校下，班级名称是否已存在
        Bjxx existingClass = classManagerMapper.findByXxdmAndBjmc(upsertDTO.getXxdm(), upsertDTO.getBjmc());
        if (existingClass != null) {
            throw new IllegalStateException("该学校下已存在同名班级：'" + upsertDTO.getBjmc() + "'");
        }

        // 校验通过，执行插入逻辑
        Bjxx bjxx = new Bjxx();
        BeanUtils.copyProperties(upsertDTO, bjxx);
        return classManagerMapper.insert(bjxx) > 0;
    }


    @Override
    @Transactional
    public boolean updateClass(ClassUpsertDTO upsertDTO, Long classId) {
        // 校验1：学校是否存在 (您的代码中已有)
        if (classManagerMapper.findSchoolByXxdm(upsertDTO.getXxdm()) < 1) {
            throw new IllegalArgumentException("所选择的学校不存在");
        }

        // [NEW] 校验2：在该学校下，班级名称是否已被其他班级占用
        Bjxx existingClass = classManagerMapper.findByXxdmAndBjmc(upsertDTO.getXxdm(), upsertDTO.getBjmc());
        // 如果找到了同名班级，并且这个班级的ID不是我们当前正在修改的这个班级ID，则说明名称冲突
        if (existingClass != null && !existingClass.getBjbs().equals(classId)) {
            throw new IllegalStateException("该学校下已存在同名班级：'" + upsertDTO.getBjmc() + "'");
        }

        // 校验通过，执行更新逻辑
        Bjxx bjxx = new Bjxx();
        BeanUtils.copyProperties(upsertDTO, bjxx);
        bjxx.setBjbs(classId); // 设置要更新的班级ID
        int rows = classManagerMapper.updateById(bjxx);
        if (rows == 0) {
            // 如果影响行数为0，说明传入的 classId 本身不存在
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
    public PageInfo<StudentForClassVO> getStudentAvailableByClassID(StudentAvailableQueryDTO query) { // [MODIFIED]
        // 开启分页
        PageHelper.startPage(query.getPageNum(), query.getPageSize());

        // 将整个DTO对象传递给Mapper
        List<StudentForClassVO> result = classManagerMapper.findAvailableStudentByClassId(query);

        // 返回分页结果
        return new PageInfo<>(result);
    }

    /**
     * [新增] 生成分配学生的Excel模板
     */
    @Override
    public byte[] generateAssignStudentTemplate(String xxdm, Integer jb) {

        UserContext.UserInfo user = UserContext.get();
        if (user != null && user.getDm() != null) {
            String permissionDm = user.getDm();
            // 如果用户有权限代码限制，且与请求的学校代码不符，则拒绝生成模板
            if (permissionDm.length() > 4) {
                xxdm = permissionDm;
            }
            if (!xxdm.startsWith(permissionDm)) {
                throw new IllegalArgumentException("无法找到相应的信息");
            }
        }

        if (!StringUtils.hasText(xxdm) || jb == null) {
            throw new IllegalArgumentException("学校代码和年级都不能为空");
        }

        // 1. 从数据库查询所有符合条件的学生
        List<ClassAssignmentData> studentList = classManagerMapper.findStudentsForClassAssignmentTemplate(xxdm, jb);

        studentList.forEach(s -> {
            String sfzjh = s.getSfzjh();
            if (sfzjh != null && sfzjh.length() >= 8) {
                s.setSfzjh(sfzjh.substring(0, 4) + "********" + sfzjh.substring(sfzjh.length() - 4));
            }
        });

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            // 2. 使用 EasyExcel 将查询到的学生列表写入 Excel
            EasyExcel.write(out, ClassAssignmentData.class)
                    .sheet("学生分班模板")
                    .doWrite(studentList);
            return out.toByteArray();
        } catch (Exception e) {
            log.error("生成分班Excel模板失败", e);
            throw new RuntimeException("生成分班Excel模板失败", e);
        }
    }

    @Override
    @Transactional
    public ImportResultVO importStudentAssignments(String xxdm, MultipartFile file) throws IOException {
        UserContext.UserInfo user = UserContext.get();
        if (user != null && user.getDm() != null) {
            String permissionDm = user.getDm();
            // 如果用户有权限代码限制，且与请求的学校代码不符，则拒绝导入
            if (!xxdm.startsWith(permissionDm)) {
                throw new IllegalArgumentException("无法找到相应的信息");
            }
        }

        if (file.isEmpty()) {
            throw new IllegalArgumentException("上传文件不能为空");
        }

        // 1. 创建监听器
        ClassAssignmentListener listener = new ClassAssignmentListener();

        // 2. 读取 Excel
        EasyExcel.read(file.getInputStream(), ClassAssignmentData.class, listener)
                .sheet()
                .doRead();

        // 3. 获取解析结果
        List<ClassAssignmentData> assignments = listener.getDataList();

        // 4. 调用你的核心逻辑
        return assignStudentsToClassBatch(xxdm, assignments);
    }

    /**
     * [NEW] 核心的批量分配逻辑
     */
    @Transactional
    public ImportResultVO assignStudentsToClassBatch(String xxdm, List<ClassAssignmentData> assignments) {
        if (CollectionUtils.isEmpty(assignments)) {
            return ImportResultVO.builder()
                    .totalRows(0)
                    .successCount(0)
                    .failureCount(0)
                    .errorMessages(Collections.emptyList())
                    .warningMessages(Collections.emptyList())
                    .build();
        }

        List<String> errorMessages = new ArrayList<>();
        List<String> warningMessages = new ArrayList<>(); // 新增
        int totalRows = assignments.size();
        int successCount = 0;

        // --- 预校验 ---
        // 过滤掉 bjmc == null 的行，记录为警告
        List<ClassAssignmentData> validAssignments = new ArrayList<>();
        for (ClassAssignmentData assignment : assignments) {
            if (!StringUtils.hasText(assignment.getBjmc())) {
                warningMessages.add("考籍号 " + assignment.getKsh() + ": 班级为空，已忽略该行");
            } else {
                validAssignments.add(assignment);
            }
        }

        // 1. 一次性查询所有班级信息
        List<Map<String, String>> schoolClassPairs = validAssignments.stream()
                .map(a -> {
                    Map<String, String> m = new HashMap<>();
                    m.put("xxdm", xxdm);
                    m.put("bjmc", a.getBjmc());
                    return m;
                })
                .distinct()
                .toList();

        List<Bjxx> existingClasses = classManagerMapper.findClassesBySchoolAndNameBatch(schoolClassPairs);
        Map<String, Bjxx> classLookupMap = existingClasses.stream()
                .collect(Collectors.toMap(b -> b.getXxdm() + "|" + b.getBjmc(), b -> b));

        // 2. 按班级ID对学生进行分组，准备批量更新
        Map<Long, List<String>> classToStudentsMap = new HashMap<>();

        for (ClassAssignmentData assignment : validAssignments) {
            String bjmc = assignment.getBjmc();
            String ksh = assignment.getKsh();

            String lookupKey = xxdm + "|" + bjmc;
            Bjxx bjxx = classLookupMap.get(lookupKey);

            if (bjxx == null) {
                errorMessages.add("考籍号 " + ksh + ": 班级 '" + bjmc + "' 在其所属学校中不存在");
            } else if (!bjxx.getJb().equals(assignment.getJb())) {
                errorMessages.add("考籍号 " + ksh + ": 学生年级(" + assignment.getJb() + ")与班级年级(" + bjxx.getJb() + ")不符");
            } else {
                classToStudentsMap.computeIfAbsent(bjxx.getBjbs(), k -> new ArrayList<>()).add(ksh);
                successCount++;
            }
        }

        // --- 执行批量更新 ---
        for (Map.Entry<Long, List<String>> entry : classToStudentsMap.entrySet()) {
            Long bjbs = entry.getKey();
            List<String> kshList = entry.getValue();

            Bjxx bjxx = classLookupMap.values().stream()
                    .filter(b -> b.getBjbs().equals(bjbs))
                    .findFirst()
                    .orElse(null);

            if (bjxx != null) {
                Map<String, Object> params = new HashMap<>();
                params.put("bjbs", bjbs);
                params.put("bjmc", bjxx.getBjmc());
                params.put("jb", bjxx.getJb());
                params.put("kshList", kshList);
                classManagerMapper.updateStudentBjbs(params);
            }
        }

        return ImportResultVO.builder()
                .totalRows(totalRows)
                .successCount(successCount)
                .failureCount(errorMessages.size())
                .errorMessages(errorMessages)
                .warningMessages(warningMessages) // 返回警告信息
                .build();
    }

    /**
     * 核心的分配逻辑，包含预校验和错误记录
     */
    @Override
    @Transactional
    public ImportResultVO assignStudentsToClass(Long bjbs, List<String> kshList) {
        if (bjbs == null) {
            throw new IllegalArgumentException("班级ID不能为空");
        }

        List<String> errorMessages = new ArrayList<>();
        int totalRows = (kshList != null) ? kshList.size() : 0;

        // 1. 校验班级是否存在
        BjxxDTO bjxx = classManagerMapper.getBjmcAndJbByBjbs(bjbs);
        if (bjxx == null) {
            throw new IllegalArgumentException("班级ID " + bjbs + " 不存在");
        }
        String bjmc = bjxx.getBjmc();
        Integer jb = bjxx.getJb();
        String xxdmOfClass = bjxx.getXxdm();

        // 2. 【预校验】一次性查询所有待分配学生的信息
        List<Ksxx> studentsInDb = new ArrayList<>();
        if (kshList != null && !kshList.isEmpty()) {
            studentsInDb = classManagerMapper.findStudentsByKshList(kshList);
        }

        Set<String> validKshInDb = studentsInDb.stream().map(Ksxx::getKsh).collect(Collectors.toSet());
        List<String> validKshForAssignment = new ArrayList<>();

        // 3. 在内存中进行逐条校验
        if (kshList != null) {
            for (String ksh : kshList) {
                Optional<Ksxx> studentOpt = studentsInDb.stream().filter(s -> s.getKsh().equals(ksh)).findFirst();

                if (studentOpt.isEmpty()) {
                    errorMessages.add("考籍号 " + ksh + ": 学生不存在");
                    continue;
                }

                Ksxx student = studentOpt.get();
                if (!xxdmOfClass.equals(student.getXxdm()) || !jb.equals(student.getJb())) {
                    errorMessages.add("考籍号 " + ksh + ": 学生所在学校或年级与目标班级不符");
                    continue;
                }

                // 校验通过
                validKshForAssignment.add(ksh);
            }
        }

        // 4. 如果所有学生都校验失败，则不执行任何数据库操作
        if (kshList != null && validKshForAssignment.isEmpty() && !kshList.isEmpty()) {
            return ImportResultVO.builder()
                    .totalRows(totalRows)
                    .successCount(0)
                    .failureCount(errorMessages.size())
                    .errorMessages(errorMessages)
                    .build();
        }


        int updatedCount = 0;
        if (!validKshForAssignment.isEmpty()) {
            Map<String, Object> param = new HashMap<>();
            param.put("bjbs", bjbs);
            param.put("bjmc", bjmc);
            param.put("jb", jb);
            param.put("kshList", validKshForAssignment);
            updatedCount = classManagerMapper.updateStudentBjbs(param);
        }

        return ImportResultVO.builder()
                .totalRows(totalRows)
                .successCount(updatedCount)
                .failureCount(errorMessages.size())
                .errorMessages(errorMessages)
                .build();
    }

    public void removeStudentsFromClass(List<String> kshList) {
        if (kshList == null || kshList.isEmpty()) {
            throw new IllegalArgumentException("考籍号列表不能为空");
        }
        classManagerMapper.removeStudentsFromClass(kshList);
    }

    public String getSchoolNameByCode(String xxdm) {
        if (!StringUtils.hasText(xxdm)) {
            return null;
        }
        return classManagerMapper.findSchoolNameByCode(xxdm); // 数据库查询 XXMC
    }
}