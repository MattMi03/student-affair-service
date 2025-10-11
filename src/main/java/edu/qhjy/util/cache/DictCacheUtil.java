package edu.qhjy.util.cache;

import edu.qhjy.util.dto.DictOptionDTO;
import edu.qhjy.util.mapper.ZDXXMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class DictCacheUtil {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ZDXXMapper zdxxMapper;

    /**
     * 初始化全部字典（系统启动时调用一次）
     */
    public void loadAllDicts() {
        System.out.println("==== 开始加载 ZDXX 字典缓存 ====");
        List<Map<String, Object>> list = zdxxMapper.selectAll();
        if (list == null || list.isEmpty()) return;

        Map<Integer, List<Map<String, Object>>> grouped =
                list.stream().collect(Collectors.groupingBy(m -> ((Number) m.get("ZDLX")).intValue()));

        grouped.forEach((zdlx, items) -> {
            // 先删除已有缓存
            redisTemplate.delete("dict:" + zdlx);
            redisTemplate.delete("dict:" + zdlx + ":reverse");

            Map<String, String> idToName = new HashMap<>();
            Map<String, String> nameToId = new HashMap<>();
            for (Map<String, Object> item : items) {
                String id = String.valueOf(item.get("XSXH"));
                String name = (String) item.get("ZDMC");
                if (id != null && name != null) {
                    idToName.put(id, name);
                    nameToId.put(name, id);
                }
            }
            redisTemplate.opsForHash().putAll("dict:" + zdlx, idToName);
            redisTemplate.opsForHash().putAll("dict:" + zdlx + ":reverse", nameToId);
        });
        System.out.println("==== 字典缓存加载完成 ====");
    }

    /**
     * 根据类型从缓存获取完整的字典选项列表
     */
    public List<DictOptionDTO> getDictOptions(Integer zdlx) {
        if (zdlx == null) return Collections.emptyList();

        // 从Redis的Hash中获取所有键值对
        Map<Object, Object> cachedMap = redisTemplate.opsForHash().entries("dict:" + zdlx);

        if (cachedMap.isEmpty()) {
            log.warn("字典类型 {} 在缓存中未命中，尝试重新加载全部字典", zdlx);
            try {
                // 加锁防止并发重复加载
                Boolean gotLock = redisTemplate.opsForValue()
                        .setIfAbsent("dict:load:lock", "1", Duration.ofSeconds(30));
                if (Boolean.TRUE.equals(gotLock)) {
                    loadAllDicts(); // 重新加载全部字典
                    redisTemplate.delete("dict:load:lock");
                } else {
                    // 等待其他线程加载完成（避免重复触发）
                    Thread.sleep(500);
                }
            } catch (Exception e) {
                log.error("重新加载字典缓存时发生异常", e);
            }

            // 再尝试取一次
            cachedMap = redisTemplate.opsForHash().entries("dict:" + zdlx);
            if (cachedMap.isEmpty()) {
                return Collections.emptyList();
            }
        }

        // 将 Map 转换为 List<DictOptionDTO>
        return cachedMap.entrySet().stream()
                .map(entry -> {
                    DictOptionDTO dto = new DictOptionDTO();
                    dto.setValue(entry.getKey().toString());
                    dto.setLabel(entry.getValue().toString());
                    return dto;
                })
                .sorted(Comparator.comparingInt(dto -> Integer.parseInt(dto.getValue()))) // 按数字排序
                .collect(Collectors.toList());
    }

    /**
     * 根据类型和ZDBS获取名称
     */
    public String getName(Integer zdlx, String zdbs) {
        if (zdlx == null || zdbs == null) return null;
        Object val = redisTemplate.opsForHash().get("dict:" + zdlx, zdbs);
        return val != null ? val.toString() : null;
    }

    /**
     * 根据类型和名称获取ZDBS
     */
    public String getCode(Integer zdlx, String name) {
        if (zdlx == null || name == null) return null;
        Object val = redisTemplate.opsForHash().get("dict:" + zdlx + ":reverse", name);
        return val != null ? val.toString() : null;
    }

    /**
     * 刷新单个类型
     */
    public void refreshDict(Integer zdlx) {
        redisTemplate.delete("dict:" + zdlx);
        redisTemplate.delete("dict:" + zdlx + ":reverse");

        List<Map<String, Object>> list = zdxxMapper.selectByZDLX(zdlx);
        if (list == null || list.isEmpty()) return;

        Map<String, String> idToName = new HashMap<>();
        Map<String, String> nameToId = new HashMap<>();
        for (Map<String, Object> item : list) {
            String id = String.valueOf(item.get("ZDBS"));
            String name = (String) item.get("ZDMC");
            idToName.put(id, name);
            nameToId.put(name, id);
        }

        redisTemplate.opsForHash().putAll("dict:" + zdlx, idToName);
        redisTemplate.opsForHash().putAll("dict:" + zdlx + ":reverse", nameToId);
    }
}