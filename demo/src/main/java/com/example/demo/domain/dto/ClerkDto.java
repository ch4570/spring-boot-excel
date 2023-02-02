package com.example.demo.domain.dto;

import com.example.demo.annotation.ExcelColumn;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@Builder
public class ClerkDto {

    @ExcelColumn(headerName = "사원번호")
    private Long id;

    @ExcelColumn(headerName = "이름")
    private String name;

    @ExcelColumn(headerName = "급여")
    private Integer salary;

    @ExcelColumn(headerName = "입사일")
    private String employDate;
}
