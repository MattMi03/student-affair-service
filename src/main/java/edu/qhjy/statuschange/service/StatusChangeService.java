package edu.qhjy.statuschange.service;

import com.github.pagehelper.PageInfo;
import edu.qhjy.statuschange.dto.*;
import edu.qhjy.statuschange.dto.audit.AuditRequestDTO;
import edu.qhjy.statuschange.dto.audit.ResumptionUpdateDTO;
import edu.qhjy.statuschange.dto.audit.TransUpdateDTO;
import edu.qhjy.statuschange.dto.audit.UserInfo;
import edu.qhjy.statuschange.vo.*;

import java.util.List;

public interface StatusChangeService {

    PageInfo<StudentBasicInfoVO> getBasicInfo(BasicInfoQueryDTO basicInfoQueryDTO);

    // --- 新生补录相关接口 ---
    void createLateRegistration(LateRegistrationApplyDTO lateRegistrationApplyDTO);

    PageInfo<LateRegistrationListVO> listLateRegistrations(CommonQueryDTO queryDTO);

    // --- 休学相关接口 ---
    PageInfo<LeaveAuditListVO> getSuspension(CommonQueryDTO commonQueryDTO);

    void createSuspension(LeaveApplyDetailDTO leaveApplyDetailDTO);

    // --- 复学相关接口 ---
    void applyForReturn(ReturnApplyDTO applyDTO);

    PageInfo<ReturnAuditListVO> listReturnApplications(CommonQueryDTO queryDTO);

    /**
     * 更新复学申请的详情（补充新年级和班级）
     *
     * @param kjydjlbs 考籍异动记录ID
     * @param dto      包含更新数据的DTO
     */
    void updateResumptionDetails(Long kjydjlbs, ResumptionUpdateDTO dto);


    // --- 转学相关接口 ---
    void updateTransDetails(Long kjydjlbs, TransUpdateDTO dto);

    PageInfo<TransferAuditListVO> getTransfer(CommonQueryDTO commonQueryDTO, Long zxlx);

    void applyTransfer(TransferApplyDetailDTO transferApplyDetailDTO, Long zxlx);

    // --- 流失相关接口 ---
    PageInfo<AttritionListVO> listAttritionApplications(CommonQueryDTO queryDTO);

    void applyForAttrition(AttritionApplyDTO applyDTO);

    // --- 出国相关接口 ---
    void applyForAbroad(AbroadApplyDTO applyDTO);

    PageInfo<AbroadListVO> listAbroadApplications(CommonQueryDTO queryDTO);

    // --- 信息变更相关接口 ---
    void applyForKeyPropertyChange(KeyPropertyChangeApplyDTO applyDTO);

    PageInfo<KeyPropertyChangeListVO> listKeyPropertyChangeApps(CommonQueryDTO queryDTO);

    // --- 公共删除方法 ---
    String deleteStatusChangeRecord(Long kjydjlbs);

    // --- 统计相关接口 ---
    PageInfo<InformationChangeSummaryVO> listInformationChangeSummary(CommonQueryDTO queryDTO);

    PageInfo<InformationChangeSummaryBySchoolVO> listInformationChangeSummaryBySchool(SummaryQueryDTO queryDTO);

    /**
     * 根据ksh查询信息变更列表
     */
    List<InformationChangeVO> getInformationChangeListByKsh(String ksh);

    /**
     * 根据ksh查询考籍异动列表
     */
    List<StatusChangeVO> getStatusChangeListByKsh(String ksh);

    // --- 审核相关接口 ---
    void auditApplication(Long kjydjlbs, AuditRequestDTO dto, UserInfo currentUser);
}
