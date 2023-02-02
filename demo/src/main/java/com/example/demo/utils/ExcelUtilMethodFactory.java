package com.example.demo.utils;

import com.example.demo.domain.dto.StudentDto;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface ExcelUtilMethodFactory {

    void studentExcelDownload(List<StudentDto> data, HttpServletResponse response);
    void renderStudentExcelBody(List<StudentDto> data, Sheet sheet, Row row, Cell cell);

}
