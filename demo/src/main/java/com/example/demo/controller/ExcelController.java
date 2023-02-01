package com.example.demo.controller;

import com.example.demo.domain.dto.StudentDto;
import com.example.demo.domain.entity.Student;
import com.example.demo.service.StudentService;
import com.example.demo.utils.ExcelUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ExcelController {

    private final StudentService studentService;


    /*
    *   엑셀 다운로드 FORM 출력
    *   @return String(View Name)
    * */
    @GetMapping("/excel/downloadForm")
    public String excelDownloadForm() {
        return "excel";
    }

    /*
    *   엑셀 다운로드
    *   @param HttpServletResponse
    *   @throws IOException
    *   @throws RuntimeException
    * */
    @GetMapping("/excel/download")
    public void excelDownLoad(HttpServletResponse response) {
        log.info("/excel/download 요청 도착!!");

        // 엑셀로 출력할 학생 리스트 조회
        List<Student> studentList = studentService.findAllStudent();

        // 학생 EntityList를 DtoList로 변환
        List<StudentDto> studentDtoList = studentList.stream()
                        .map(s -> s.toDto())
                        .collect(Collectors.toList());

        // 엑셀 다운로드 로직 실행
        ExcelUtils.excelDownload(studentDtoList, response);
    }
}
