package com.example.demo.controller;

import com.example.demo.domain.dto.ClerkDto;
import com.example.demo.domain.dto.StudentDto;
import com.example.demo.domain.entity.Clerk;
import com.example.demo.domain.entity.CompareStudent;
import com.example.demo.domain.entity.Student;
import com.example.demo.service.ClerkService;
import com.example.demo.service.CompareStudentService;
import com.example.demo.service.StudentService;
import com.example.demo.utils.ExcelUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ExcelController {

    private final StudentService studentService;
    private final ExcelUtils excelUtils;
    private final ClerkService clerkService;
    private final CompareStudentService compareService;


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
    *  @return String(View Name)
    * */
    @GetMapping("/excel/readForm")
    public String excelReadForm() {
        return "excelReadForm";
    }

    /*
     *  엑셀 비교 FORM 출력
     *  @return String(View Name)
     * */
    @GetMapping("/excel/compareForm")
    public String excelCompareForm() {
        return "excelDataCompareForm";
    }

    /*
     *   엑셀과 현재 데이터 비교 출력
     *   @param Model
     *   @param MultipartFile
     *   @return String(View-Name)
     *   @throws IOException
     *   @throws RuntimeException
     * */
    @PostMapping("/excel/compare")
    public String excelCompare(String mode, MultipartFile excelFile, Model model, HttpServletResponse response) {

        // DB에서 데이터 비교용 테이블의 데이터를 삭제한다.
        compareService.removeAllCompareData();

        // 넘어온 엑셀 파일의 데이터를 읽는다.
        List<StudentDto> studentExcel = (List<StudentDto>)readExcel(mode, excelFile);

        // 현재 시점의 DB 데이터를 수집한다.
        List<StudentDto> dataStudentList = studentService.findAllStudent()
                .stream().map(s -> s.toDto()).collect(Collectors.toList());

        // 읽어온 데이터와 DB 데이터를 비교해서 차이나는 부분만 뽑아낸다.
        List<CompareStudent> compareStudentList = studentExcel.stream()
                                                .filter(s -> dataStudentList.stream().
                                                        noneMatch(Predicate.isEqual(s)))
                                                .map(s -> s.toEntity())
                                                .collect(Collectors.toList());

        // 비교용 테이블에 데이터를 INSERT 한다.
        compareService.saveAllCompareStudent(compareStudentList);

        model.addAttribute("compareExcel", compareStudentList);

        return "excelCompareDataForm";
    }

    /*
     *   엑셀과 현재 데이터 비교된 것 엑셀 다운로드
     *   @param List<StudentDto>
     *   @throws IOException
     *   @throws RuntimeException
     * */
    @GetMapping("/excel/compareDownload")
    public void excelCompareDownLoad(HttpServletResponse response) {

        // 엑셀을 업로드하여 DB와 비교한 데이터를 list에 담는다.
        List<StudentDto> list = compareService.findAllCompareStudent()
                                                .stream()
                                                .map(s -> s.toDto())
                                                .collect(Collectors.toList());

        // 엑셀 다운로드를 수행한다.
        excelUtils.studentExcelDownload(list, response);
    }

    /*
     *   모드별 엑셀 읽기 로직
     *   @param String - mode
     *   @param MultipartFile
     *   @param HttpServletResponse
     *   @throws IOException
     *   @throws RuntimeException
     *   @return List<?>
     * */
    private List<?> readExcel(String mode, MultipartFile excelFile) {
        // 매개변수로 들어오는 mode의 값에 따라서 다른 로직이 수행된다.
        switch (mode) {
            // mode가 "student"라면 학생 엑셀을 읽는 로직이 수행된다.
            case "student" :
                // 파일로 넘어온 학생 엑셀파일을 읽어서 List로 반환받는다.
                List<StudentDto> studentExcel = excelUtils.readStudentExcel(excelFile);

                for(StudentDto s : studentExcel) {
                    log.info("학생 출력 = {}", s);
                }
                return studentExcel;


            // mode가 "clerk"라면 사원 엑셀을 읽는 로직이 수행된다.
            case "clerk" :
                // 파일로 넘어온 사원 엑셀파일을 읽어서 List로 반환받는다.
                List<ClerkDto> clerkExcel = excelUtils.readClerkExcel(excelFile);

                for(ClerkDto c : clerkExcel) {
                    log.info("사원 출력 = {}", c);
                }
                return clerkExcel;

            // 해당하지 않으면 예외를 던진다.
            default :
                throw new IllegalStateException("존재하지 않는 옵션입니다. 확인 후 다시 진행해주세요.");

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
