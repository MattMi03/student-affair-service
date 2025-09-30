package edu.qhjy.rollcall.service.impl;

import com.alibaba.excel.EasyExcel;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import edu.qhjy.rollcall.domain.Kqjl;
import edu.qhjy.rollcall.dto.RollCallImportDTO;
import edu.qhjy.rollcall.dto.StudentRollCallExcelQueryDTO;
import edu.qhjy.rollcall.dto.StudentRollCallQueryDTO;
import edu.qhjy.rollcall.mapper.StudentRollCallMapper;
import edu.qhjy.rollcall.service.IStudentRollCallService;
import edu.qhjy.rollcall.service.RollCallImportListener;
import edu.qhjy.rollcall.vo.StudentRollCallListVO;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentRollCallServiceImpl implements IStudentRollCallService {

    private final StudentRollCallMapper studentRollCallMapper;

    @Override
    public PageInfo<StudentRollCallListVO> listStudents(StudentRollCallQueryDTO query) {
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<StudentRollCallListVO> list = studentRollCallMapper.findForPage(query);
        return new PageInfo<>(list);
    }

    @Override
    public void downloadStudentList(StudentRollCallExcelQueryDTO query, HttpServletResponse response) {
        // 1. 从数据库获取数据
        List<StudentRollCallListVO> dataToExport = studentRollCallMapper.findForPageForExcel(query);

        // 2. 构建动态文件名
        // 获取学校名，如果没数据就用默认名
        String schoolName = dataToExport.isEmpty() ? "学校" : dataToExport.get(0).getXxmc();
        StringBuilder fileNameBuilder = new StringBuilder(schoolName);

        // 如果有年级就加上
        if (query.getJb() != null) {
            fileNameBuilder.append("_").append(query.getJb()).append("级");
        }

        // 如果有班级并且有数据，就加上班级名
        if (query.getBjbs() != null && !dataToExport.isEmpty()) {
            fileNameBuilder.append("_").append(dataToExport.get(0).getBjmc());
        }

        fileNameBuilder.append("考生点名册");

        // 加上当前日期
        String dateStr = LocalDate.now().toString(); // yyyy-MM-dd
        fileNameBuilder.append("_").append(dateStr);

        // 3. 设置HTTP响应头并写入Excel数据流
        try {
            // URL编码文件名，防止中文乱码，并处理空格
            String fileName = URLEncoder.encode(fileNameBuilder.toString(), StandardCharsets.UTF_8.name())
                    .replaceAll("\\+", "%20");

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

            // 使用 try-with-resources 语句确保输出流被自动关闭，这是一种更安全的方式
            try (ServletOutputStream out = response.getOutputStream()) {
                EasyExcel.write(out, StudentRollCallListVO.class)
                        .sheet("考生名单")
                        .doWrite(dataToExport);
            }
            // 当这里的try-with-resources块执行完毕，即使发生异常，`out.close()`也会被自动调用

        } catch (IOException e) {
            // 这是最关键的异常捕获。
            // 如果捕获到IOException，极有可能是客户端在下载完成前关闭了连接。
            // 我们记录日志，然后将其包装成一个RuntimeException向上抛出。
            // 最终这个异常会被我们之前定义的GlobalExceptionHandler捕获。
            log.warn("文件下载期间发生IO错误，可能原因：客户端提前关闭了连接。错误信息: {}", e.getMessage());
            throw new RuntimeException("文件下载IO异常", e);

        } catch (Exception e) {
            // 捕获其他所有可能的异常，例如EasyExcel在转换数据模型时出错。
            // 同样记录日志并向上抛出，交由全局异常处理器处理。
            log.error("生成或下载Excel文件时发生未知错误。", e);
            throw new RuntimeException("生成Excel文件失败", e);
        }
    }

    @Override
    @Transactional
    public Map<String, Object> importRollCall(List<RollCallImportDTO> importList) {
        if (CollectionUtils.isEmpty(importList)) {
            throw new IllegalArgumentException("导入的数据列表不能为空");
        }

        // 1️⃣ 收集导入考号
        Set<String> kshSet = importList.stream()
                .map(RollCallImportDTO::getKsh)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (kshSet.isEmpty()) {
            throw new IllegalArgumentException("导入列表中没有有效考籍号");
        }

        // 2️⃣ 查询数据库中存在的考号
        List<String> existingKshs = studentRollCallMapper.findExistingKshs(new ArrayList<>(kshSet));
        Set<String> existingKshSet = new HashSet<>(existingKshs);

        // 3️⃣ 过滤掉不存在的考号，并收集错误信息
        List<RollCallImportDTO> validRecords = new ArrayList<>();
        List<String> invalidKshs = new ArrayList<>();
        for (RollCallImportDTO dto : importList) {
            if (existingKshSet.contains(dto.getKsh())) {
                validRecords.add(dto);
            } else {
                invalidKshs.add("找不到考籍号: " + dto.getKsh());
            }
        }

        if (validRecords.isEmpty()) {
            throw new IllegalArgumentException("导入失败：以下考籍号不存在 - " + String.join(",", invalidKshs));
        }

        // 4️⃣ 批量插入去重当天重复记录（直接在 SQL 里去重）
        List<Kqjl> recordsToInsert = validRecords.stream()
                .map(dto -> {
                    Kqjl record = new Kqjl();
                    record.setKsh(dto.getKsh());
                    record.setKqzt(dto.getKqzt());
                    record.setKqsj(dto.getKqsj() != null ? dto.getKqsj() : LocalDateTime.now());
                    record.setZqry(dto.getZqry());
                    return record;
                })
                .collect(Collectors.toList());

        if (!recordsToInsert.isEmpty()) {
            studentRollCallMapper.batchInsertIgnoreDuplicates(recordsToInsert);
        }

        // 5️⃣ 返回统计信息
        Map<String, Object> result = new HashMap<>();
        result.put("totalCount", importList.size());
        result.put("successCount", recordsToInsert.size());
        result.put("failCount", importList.size() - recordsToInsert.size());
        result.put("invalidKshs", invalidKshs);
        return result;
    }

    @Override
    public Map<String, Object> importRollCallFromExcel(MultipartFile file) throws IOException {
        RollCallImportListener listener = new RollCallImportListener(this);
        EasyExcel.read(file.getInputStream(), StudentRollCallListVO.class, listener).sheet().doRead();
        return listener.getImportResult();
    }

    @Override
    @Transactional
    public Map<String, Object> processExcelData(List<StudentRollCallListVO> data) {
        Map<String, Object> resultMap = new HashMap<>();
        List<String> errors = new ArrayList<>();

        // 1. 过滤掉无效数据
        List<StudentRollCallListVO> validData = data.stream()
                .filter(item -> item.getKsh() != null && !item.getKsh().trim().isEmpty() &&
                        item.getKqzt() != null && !item.getKqzt().trim().isEmpty())
                .collect(Collectors.toList());

        data.stream()
                .filter(item -> (item.getKsh() == null || item.getKsh().trim().isEmpty()) ||
                        (item.getKqzt() == null || item.getKqzt().trim().isEmpty()))
                .forEach(invalidItem -> errors.add("数据无效(考号或考勤状态为空): 考号 " + invalidItem.getKsh() + ", 姓名 " + invalidItem.getXm()));

        if (validData.isEmpty()) {
            resultMap.put("totalCount", data.size());
            resultMap.put("successCount", 0);
            resultMap.put("failCount", data.size());
            resultMap.put("errors", errors);
            return resultMap;
        }

        // 2. 校验考号是否存在
        Set<String> kshSet = validData.stream().map(StudentRollCallListVO::getKsh).collect(Collectors.toSet());
        List<String> existingKshs = studentRollCallMapper.findExistingKshs(new ArrayList<>(kshSet));
        Set<String> existingKshSet = new HashSet<>(existingKshs);

        // 3. 分离出考号真实存在的记录
        List<StudentRollCallListVO> recordsToProcess = new ArrayList<>();
        validData.forEach(item -> {
            if (existingKshSet.contains(item.getKsh())) {
                recordsToProcess.add(item);
            } else {
                errors.add("考号不存在: " + item.getKsh());
            }
        });

        // 4. 将VO转换为要插入数据库的Kqjl实体
        List<Kqjl> recordsToInsert = recordsToProcess.stream()
                .map(vo -> {
                    Kqjl record = new Kqjl();
                    record.setKsh(vo.getKsh());
                    record.setKqzt(vo.getKqzt());
                    record.setKqsj(vo.getKqsj() != null ? vo.getKqsj() : LocalDateTime.now());
                    record.setZqry(vo.getZqry());
                    return record;
                })
                .collect(Collectors.toList());

        // 5. 执行批量插入
        int successCount = 0;
        if (!recordsToInsert.isEmpty()) {
            // **【核心修改】**
            // 调用 void 方法，不再接收返回值
            studentRollCallMapper.batchInsertIgnoreDuplicates(recordsToInsert);
            // 将尝试插入的记录数作为成功计数
            successCount = recordsToInsert.size();
        }

        // 6. 组装返回结果
        resultMap.put("totalCount", data.size());
        resultMap.put("successCount", successCount);
        resultMap.put("failCount", data.size() - successCount);
        resultMap.put("errors", errors);

        return resultMap;
    }
}