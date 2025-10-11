package edu.qhjy.util.service;

import edu.qhjy.util.dto.DictOptionDTO;

import java.util.List;

public interface DictService {
    /**
     * 根据字典类型字符串获取下拉选项列表
     *
     * @param dictType 字典类型 (例如 "MZ", "ZZMM")
     * @return 下拉选项列表
     */
    List<DictOptionDTO> getDictOptionsByType(String dictType);
}