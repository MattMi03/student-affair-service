package edu.qhjy.util.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class DictOptionDTO {
    @JsonIgnore
    private String value; // ZDBS

    private String label; // ZDMC
}