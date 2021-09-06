package com.github.schedule.workmonth.dto.request.workmonth;

import com.github.schedule.workmonth.validation.constraint.UniqueIdentifier;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkMonthCreateDto {

    @UniqueIdentifier
    private String userId;

    @NotNull
    private Integer year;

    @NotNull
    private Integer month;
}