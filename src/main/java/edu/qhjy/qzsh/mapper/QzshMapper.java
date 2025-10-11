package edu.qhjy.qzsh.mapper;

import com.github.pagehelper.Page;
import edu.qhjy.qzsh.domain.Qzsh;
import edu.qhjy.qzsh.dto.QzshQueryDTO;
import edu.qhjy.qzsh.vo.QzshListVO;
import jakarta.validation.constraints.NotNull;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface QzshMapper {
    Page<QzshListVO> findForPage(QzshQueryDTO query);

    Qzsh findById(@Param("ksbs") Long ksbs);

    int existsById(@Param("ksbs") Long ksbs);

    int insert(Qzsh qzsh);

    int update(Qzsh qzsh);

    int deleteById(@Param("ksbs") Long ksbs);

    int deleteBatchByIds(@Param("ids") List<Long> ids);

    int existsByKsbs(@NotNull(message = "考生标识(ksbs)不能为空") Long ksbs);

    String findKshByKsbs(@Param("ksbs") Long ksbs);
}