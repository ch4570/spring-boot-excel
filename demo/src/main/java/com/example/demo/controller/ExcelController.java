package com.example.demo.controller;

import com.example.demo.domain.dto.ClerkDto;
import com.example.demo.domain.dto.StudentDto;
import com.example.demo.domain.entity.Clerk;
import com.example.demo.domain.entity.Student;
import com.example.demo.service.ClerkService;
import com.example.demo.service.StudentService;
import com.example.demo.utils.ExcelUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ExcelController {

    private final StudentService studentService;
    private final ExcelUtils excelUtils;
    private final ClerkService clerkService;


    /*
    *   엑셀 다운로드 FORM 출력
    *   @return String(View Name)
    * */
    @GetMapping("/excel/downloadForm")
    public String excelDownloadForm() {
        return "excelDownloadForm";
    }

    /*
    *   엑셀 다운로드
    *   @param HttpServletResponse
    *   @throws IOException
    *   @throws RuntimeException
    * */
    @GetMapping("/excel/download")
    public void excelDownLoad(HttpServletResponse response, String mode) {
        // 엑셀 다운로직 실행(Mode에 따라 수행되는 로직 변경되어 메서드 분리)
        downLoadExcel(mode, response);
    }

    /*
    *  엑셀 읽기 FORM 출력
    *  return String(View Name)
    * */
    @GetMapping("/excel/readForm")
    public String excelReadForm() {
        return "excelReadForm";
    }

    /*
    *   엑셀 파일 읽기
    *   @param Model
    *   @param MultipartFile
    *   @return String(View Name)
    *   @throws IOException
    *   @throws RuntimeException
    * */
    @PostMapping("/excel/read")
    public void excelRead(String mode, Model model, MultipartFile excelFile) {
        // mode에 따라 엑셀 파일 읽기 메서드 실행
        readExcel(mode, excelFile, model);
    }


    /*
     *   모드별 엑셀 읽기 로직
     *   @param String - mode
     *   @param MultipartFile
     *   @param HttpServletResponse
     *   @throws IOException
     *   @throws RuntimeException
     * */
    private void readExcel(String mode, MultipartFile excelFile, Model model) {
        // 매개변수로 들어오는 mode의 값에 따라서 다른 로직이 수행된다.
        switch (mode) {
            // mode가 "student"라면 학생 엑셀을 읽는 로직이 수행된다.
            case "student" :
                // 파일로 넘어온 학생 엑셀파일을 읽어서 List로 반환받는다.
                List<StudentDto> studentExcel = excelUtils.readStudentExcel(excelFile);
                model.addAttribute("studentExcel", studentExcel);

                for(StudentDto s : studentExcel) {
                    log.info("학생 출력 = {}", s);
                }
                break;

            // mode가 "clerk"라면 사원 엑셀을 읽는 로직이 수행된다.
            case "clerk" :
                // 파일로 넘어온 사원 엑셀파일을 읽어서 List로 반환받는다.
                List<ClerkDto> clerkExcel = excelUtils.readClerkExcel(excelFile);
                model.addAttribute("clerkExcel", clerkExcel);

                for(ClerkDto c : clerkExcel) {
                    log.info("사원 출력 = {}", c);
                }
        }
    }


    /*
    *   모드별 엑셀 다운로드 로직
    *   @param String - mode
    *   @param HttpServletResponse
    *   @throws IOException
    *   @throws RuntimeException
    * */
    private void downLoadExcel(String mode, HttpServletResponse response) {
        // 매개변수로 들어오는 mode의 값에 따라서 다른 로직이 수행된다.
        switch (mode) {
            // mode가 "student"라면 학생 데이터를 엑셀로 다운로드
            case "student" :
                log.info("학생 엑셀 다운로드 요청 도착!!");

                // 엑셀로 출력할 학생 리스트 조회
                List<Student> studentList = studentService.findAllStudent();

                // 학생 EntityList를 DtoList로 변환
                List<StudentDto> studentDtoList = studentList.stream()
                        .map(s -> s.toDto())
                        .collect(Collectors.toList());

                // 학생 엑셀 다운로드 로직 실행
                excelUtils.studentExcelDownload(studentDtoList, response);
                break;

            // mode가 "clerk"라면 사원 데이터를 엑셀로 다운로드
            case "clerk" :
                log.info("사원 엑셀 다운로드 요청 도착!!");

                // 엑셀로 출력할 학생 리스트 조회
                List<Clerk> clerkList = clerkService.findAllClerk();

                // 학생 EntityList를 DtoList로 변환
                List<ClerkDto> clerkDtoList = clerkList.stream()
                        .map(s -> s.toDto())
                        .collect(Collectors.toList());

                // 사원 엑셀 다운로드 로직 실행
                excelUtils.clerkExcelDownload(clerkDtoList, response);
                break;
        }
    }
}
