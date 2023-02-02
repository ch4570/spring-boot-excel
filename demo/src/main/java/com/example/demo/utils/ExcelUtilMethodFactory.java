package com.example.demo.utils;

import com.example.demo.domain.dto.ClerkDto;
import com.example.demo.domain.dto.StudentDto;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface ExcelUtilMethodFactory {

    void studentExcelDownload(List<StudentDto> data, HttpServletResponse response);
    void renderStudentExcelBody(List<StudentDto> data, Sheet sheet, Row row, Cell cell);
    List<StudentDto> readStudentExcel(MultipartFile file);

    void clerkExcelDownload(List<ClerkDto> data, HttpServletResponse response);
    void renderClerkExcelBody(List<ClerkDto> data, Sheet sheet, Row row, Cell cell);
    List<ClerkDto> readClerkExcel(MultipartFile file);


}
