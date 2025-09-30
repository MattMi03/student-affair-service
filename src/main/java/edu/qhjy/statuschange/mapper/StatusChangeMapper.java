package edu.qhjy.statuschange.mapper;

import edu.qhjy.statuschange.domain.*;
import edu.qhjy.statuschange.dto.BasicInfoQueryDTO;
import edu.qhjy.statuschange.dto.CommonQueryDTO;
import edu.qhjy.statuschange.dto.SummaryQueryDTO;
import edu.qhjy.statuschange.dto.delete.KjydjlRecordDTO;
import edu.qhjy.statuschange.vo.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StatusChangeMapper {
    List<StudentBasicInfoVO> getBasicInfoByKsh(BasicInfoQueryDTO basicInfoQueryDTO);

    // 新生补录
    List<LateRegistrationListVO> selectLateRegistrationList(CommonQueryDTO queryDTO);

    // 休学申请
    List<LeaveAuditListVO> getSuspension(CommonQueryDTO commonQueryDTO);

    void insertXfxjl(Xfxjl xfxjl);

    // 复学申请
    List<ReturnAuditListVO> selectReturnList(CommonQueryDTO queryDTO);

    /**
     * 更新 xfxjl 表的记录
     *
     * @param xfxjl 包含最新数据的实体
     * @return 更新的行数
     */
    int updateXfxjlDetails(Xfxjl xfxjl);

    // 转学申请

    void updateZxjlDetails(Zxjl resumptionDetail);

    List<TransferAuditListVO> getTransfer(
            @Param("common") CommonQueryDTO commonQueryDTO,
            @Param("zxlx") Long zxlx
    );

    void insertZxjl(Zxjl zxjl);

    // 流失登记
    List<AttritionListVO> selectAttritionList(CommonQueryDTO queryDTO);

    void insertLsjl(Lsjl lsjl);

    // 出国登记
    List<AbroadListVO> selectAbroadList(CommonQueryDTO queryDTO);

    void insertAbroadRegistration(Cgdj cgdj);

    // 信息变更登记
    void insertKeyPropertyChange(Xxbgjl detail);

    List<KeyPropertyChangeListVO> selectKeyPropertyChangeList(CommonQueryDTO queryDTO);


    // 公共方法
    void insertKjydjl(Kjydjl Kjydjl);

    KjydjlRecordDTO getKjydlxbsByKjydjlbs(Long kjydjlbs);

    void deleteXfxjlByKjydjlbs(Long kjydjlbs);

    void deleteZxjlByKjydjlbs(Long kjydjlbs);

    void deleteLsjlByKjydjlbs(Long kjydjlbs);

    void deleteXxbgjlByKjydjlbs(Long kjydjlbs);

    void deleteKjydjlById(Long kjydjlbs);

    // 统计相关
    List<InformationChangeSummaryVO> selectInformationChangeSummary(CommonQueryDTO queryDTO);

    List<InformationChangeSummaryBySchoolVO> selectInformationChangeSummaryBySchool(SummaryQueryDTO queryDTO);

    Xfxjl getLatestXfxjlByKjydjlbs(@NotBlank(message = "考生号不能为空") String ksh);

    /**
     * 根据考生号(ksh)查询信息变更列表
     *
     * @param ksh 考生号
     * @return 信息变更记录列表
     */
    List<InformationChangeVO> selectInformationChangeByKsh(@Param("ksh") String ksh);

    /**
     * 根据考生号(ksh)查询考籍异动列表
     *
     * @param ksh 考生号
     * @return 考籍异动记录列表
     */
    List<StatusChangeVO> selectStatusChangeByKsh(@Param("ksh") String ksh);

    void updateAuditInfo(Kjydjl application);

    /**
     * 根据主键ID查询考籍异动记录
     *
     * @param kjydjlbs 记录ID
     * @return Kjydjl 实体
     */
    Kjydjl findKjydjlById(@Param("kjydjlbs") Long kjydjlbs);


    /**
     * 根据异动记录ID查询考生号
     *
     * @param kjydjlbs 记录ID
     * @return 考生号 (ksh)
     */
    String findKshByKjydjlbs(@Param("kjydjlbs") Long kjydjlbs);


    // === 用于善后处理器查询详情的方法 ===

    /**
     * 根据 kjydjlbs 查找最新的休复学详情
     */
    Xfxjl findXfxjlByKjydjlbs(@Param("kjydjlbs") Long kjydjlbs);

    /**
     * 根据 kjydjlbs 查找最新的转学详情
     */
    Zxjl findZxjlByKjydjlbs(@Param("kjydjlbs") Long kjydjlbs);

    /**
     * 根据 kjydjlbs 查找所有信息变更记录
     */
    List<Xxbgjl> findXxbgjlByKjydjlbs(@Param("kjydjlbs") Long kjydjlbs);

    /**
     * 检查指定学生是否有正在进行中的、特定类型的异动申请
     *
     * @param ksh      考生号
     * @param kjydlxbs 异动类型ID
     * @return 存在的待审核申请数量
     */
    int countPendingApplications(@Param("ksh") String ksh, @Param("kjydlxbs") Long kjydlxbs);

    /**
     * 查询一个学生最新的、已生效的休学或复学记录
     *
     * @param ksh 考生号
     * @return 最新的 xfxjl 记录
     */
    Xfxjl findLatestEffectiveSuspensionOrResumption(@Param("ksh") String ksh);

    String findSchoolNameByBjbs(@NotNull(message = "新班级不能为空") Long xbjbs);

    void setFxkshByKjydjlbs(Long kjydjlbs, String fxksh, String xxsc);

    int findIfExistByKsh(@NotBlank(message = "考生号不能为空") String ksh);

    int findIfSchoolExistBySchoolName(String xxmc);

}

