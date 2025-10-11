package edu.qhjy.util.service.impl;

import edu.qhjy.util.cache.DictCacheUtil;
import edu.qhjy.util.constants.DictTypeConstants;
import edu.qhjy.util.dto.DictOptionDTO;
import edu.qhjy.util.service.DictService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DictServiceImpl implements DictService {

    private final DictCacheUtil dictCacheUtil;

    @Override
    public List<DictOptionDTO> getDictOptionsByType(String dictType) {
        // 将前端传入的字符串类型转换为整数常量
        Integer zdlx = switch (dictType.toUpperCase()) {
            case "MZ" -> DictTypeConstants.MZ;
            case "ZZMM" -> DictTypeConstants.ZZMM;
            case "XJZT" -> DictTypeConstants.XJZT;
            case "KSLX" -> DictTypeConstants.KSLX;
            case "RXFS" -> DictTypeConstants.RXFS;
            case "XBLX" -> DictTypeConstants.XBLX;
            default -> throw new IllegalArgumentException("非法访问");
        };

        // 调用 DictCacheUtil 从缓存中获取数据
        return dictCacheUtil.getDictOptions(zdlx);
    }
}