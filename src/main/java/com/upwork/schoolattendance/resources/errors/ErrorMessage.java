package com.upwork.schoolattendance.resources.errors;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private String code;
    private String message;

    @Nullable
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> details = new ArrayList<>();
}
