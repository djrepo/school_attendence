package com.upwork.schoolattendance.resources.model;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;

@Getter
@Setter
@ApiModel(value = "com.upwork.schoolattendance.resources.qrcode.model.CurrentActivityInput")
public class ClassroomActivityRequest {
    @NotEmpty
    private String qrCode;

    @NotNull
    @Min(1)
    private Long userId;
    @NotNull
    private Double latitude;
    @NotNull
    private Double longitude;
}
