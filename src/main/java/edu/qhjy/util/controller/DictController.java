package edu.qhjy.util.controller;

import edu.qhjy.util.dto.DictOptionDTO;
import edu.qhjy.util.service.DictService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dict")
@RequiredArgsConstructor
public class DictController {

    private final DictService dictService;

    @Operation(summary = "根据字典类型获取下拉列表")
    @GetMapping("/{dictType}/options")
    public List<DictOptionDTO> getDictOptions(@PathVariable String dictType) {
        return dictService.getDictOptionsByType(dictType);
    }
}