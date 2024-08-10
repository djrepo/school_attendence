package com.upwork.schoolattendance.resources.model;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ApiModel(value = "com.upwork.schoolattendance.resources.qrcode.model.CheckInInput")
public class CheckInRequest {

    @NotNull
    @Min(1)
    private Long userId;

    @NotNull
    @Min(1)
    private Long lessonId;

    @NotEmpty
    private String token;

}
