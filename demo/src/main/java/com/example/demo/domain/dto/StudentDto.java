package com.example.demo.domain.dto;

import com.example.demo.annotation.ExcelColumn;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@Builder
public class StudentDto {

    @ExcelColumn(headerName = "반")
    private Long ban;

    @ExcelColumn(headerName = "이름")
    private String name;
}
