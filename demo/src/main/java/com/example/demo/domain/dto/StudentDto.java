package com.example.demo.domain.dto;

import com.example.demo.annotation.ExcelColumn;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class StudentDto {

    @ExcelColumn(headerName = "반")
    private Long ban;

    @ExcelColumn(headerName = "이름")
    private String name;
}
