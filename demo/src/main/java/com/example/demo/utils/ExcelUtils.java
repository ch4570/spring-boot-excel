package com.example.demo.utils;

import com.example.demo.annotation.ExcelColumn;
import com.example.demo.domain.dto.ClerkDto;
import com.example.demo.domain.dto.StudentDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tika.Tika;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ExcelUtils implements ExcelUtilMethodFactory {

    /*
    *   학생 엑셀 다운로드 수행 로직
    *   @param List<StudentDto>
    *   @param HttpServletResponse
    *   @throws IOException
    *   @throws RuntimeException
    * */
    @Override
    public void studentExcelDownload(List<StudentDto> data, HttpServletResponse response) {
        // 엑셀파일(Workbook) 객체 생성
        Workbook workbook =  getXSSFWorkBook();

        // 엑셀파일 sheet를 만들고, sheet의 이름을 지정해 줄 수 있다.
        Sheet sheet = workbook.createSheet("첫 번째 시트");

        // 엑셀의 열에 해당하는 Cell 객체 생성
        Cell cell = null;

        // 엑셀의 행에 해당하는 Row 객체 생성
        Row row = null;

        // List가 아닌 DTO를 넘겨줘야 하므로 메서드를 통해 DTO의 class 정보가 담긴 class 객체를 넣어준다.
        // Header의 내용을 List로 반환 받는다(엑셀의 Cell의 첫줄이 된다.)
        List<String> excelHeaderList = getHeaderName(getClass(data));

        // Header - 열의 첫줄(컬럼 이름들)을 그리는 작업

        // 첫 행을 생성해준다.
        row = sheet.createRow(0);

        // 헤더의 수(컬럼 이름의 수)만큼 반복해서 행을 생성한다.
        for(int i=0; i<excelHeaderList.size(); i++) {

            // 열을 만들어준다.
            cell = row.createCell(i);

            // 열에 헤더이름(컬럼 이름)을 넣어준다.
            cell.setCellValue(excelHeaderList.get(i));
        }

        // Body
        // 헤더 밑의 엑셀 파일 내용부분에 들어갈 내용을 그리는 작업
        renderStudentExcelBody(data, sheet, row, cell);

        // DownLoad
        // 엑셀 파일이 완성 되면 파일 다운로드를 위해 content-type과 Header를 설정해준다.
        // filename=파일이름.xlsx 가 파일의 이름이 된다.
        response.setContentType("ms-vnd/excel");
        response.setHeader("Content-Disposition", "attachment;filename=student.xlsx");

        try {
            // 엑셀 파일을 다운로드 하기 위해 write() 메서드를 사용한다.
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            // checked 예외를 사용하면 추후 의존이나 예외 누수 문제가 생길 수 있으므로
            // RuntimeException으로 한번 감싸서, cause가 나올 수 있게 발생한 예외를 넣어준다.
            log.error("Workbook write 수행 중 IOException 발생!");
            throw new RuntimeException(e);
        } finally {
            // 파일 입출력 스트림을 사용한 후에는 예외 발생 여부와 관계없이 반드시 닫아 주어야 한다.
            closeWorkBook(workbook);
        }
    }

    /*
     *   엑셀의 본문에 내용을 그려주는 로직
     *   @param List<StudentDto>
     *   @param Sheet
     *   @param Row
     *   @param Cell
     * */
    @Override
    public void renderStudentExcelBody(List<StudentDto> data, Sheet sheet, Row row, Cell cell) {
        // 현재 행의 개수를 가지고 있는 변수 rowCount 선언(Header를 그리고 시작했으므로 1부터 시작)
        int rowCount = 1;

        // 조회해온 데이터 리스트(List<StudentDto>)의 크기만큼 반복문을 실행한다.
        for(StudentDto student : data) {

            // 헤더를 설정할때 0번 인덱스가 사용 되었으므로, i값에 1을 더해서 1번 로우(행)부터 생성한다.
            row = sheet.createRow(rowCount++);

            // TODO : 하드코딩 대신 추후 동적으로 처리 할 수 있도록 개선 예정
            // 첫 번째 cell(열)을 생성한다.
            cell = row.createCell(0);
            // 첫 번째 cell(열)의 값을 셋팅한다.
            cell.setCellValue(student.getBan());
            // 두 번째 cell(열)을 생성한다.
            cell = row.createCell(1);
            // 두 번째 cell(열)의 값을 셋팅한다.
            cell.setCellValue(student.getName());
        }
    }

    /*
    *  학생 엑셀을 읽어서 List로 반환해주는 로직
    *  @param MulitpartFile
    *  @return List<StudentDto>
    *  @throws IOException
    *  @throws IllegalStateException
    * */
    @Override
    public List<StudentDto> readStudentExcel(MultipartFile file) {

        List<StudentDto> studentDtoList = new ArrayList<>();

        // 파일의 확장자만 바꿔서 올리는 FakeFile을 방지하기 위해 Apache Tika 사용
        Tika tika = getTika();

        // try ~ catch ~ finally 블럭에서 사용하기 위해 Workbook 초기화
        Workbook workbook = null;

        try {
            // Tika를 사용해서 MIME 타입을 얻어낸다
            String mimeType = tika.detect(file.getBytes());
            log.info("mimeType = {}", mimeType);


            // 파일의 확장자 명을 구한다
            // ex) Student.xlsx => return "xlsx"
            String extension = FilenameUtils.getExtension(file.getOriginalFilename());
            log.info("extension = {}", extension);

            // 파일의 확장자명과 MIME 타입으로 엑셀파일이 맞는지 검증한다.
            if(!isExcelPresent(mimeType, extension)) {
                throw new IllegalStateException("엑셀 파일이 아닙니다. 확인 후 다시 업로드 해주세요.");
            }

            // 가져온 파일로 Workbook 객체를 생성
            workbook = new XSSFWorkbook(file.getInputStream());

            // 현재 시트를 첫번째 시트만 사용중이므로 0번 인덱스 시트를 가져온다.
            Sheet sheet = workbook.getSheetAt(0);

            // 헤더를 제외하고 데이터를 뽑아야 하므로, 1번 인덱스 부터 시작한다.
            for(int i=1; i<sheet.getPhysicalNumberOfRows(); i++) {

                // 시트로부터 행을 가져온다.
                Row row = sheet.getRow(i);

                // Cell들의 값을 얻어와서 StudentDto와 매핑해준다.
                StudentDto studentDto = StudentDto.builder()
                        .ban((long)row.getCell(0).getNumericCellValue())
                        .name(row.getCell(1).getStringCellValue())
                        .build();

                // 반환할 studentDtoList에 추가한다.
                studentDtoList.add(studentDto);
            }
        } catch (IOException e) {
            // checked 예외를 사용하면 추후 의존이나 예외 누수 문제가 생길 수 있으므로
            // RuntimeException으로 한번 감싸서, cause가 나올 수 있게 발생한 예외를 넣어준다.
            log.error("엑셀 파일을 읽는 도중 IOException 발생!");
            throw new RuntimeException(e);
        } finally {
           closeWorkBook(workbook);
        }


        return studentDtoList;
    }

    /*
     *   사원 엑셀 다운로드 수행 로직
     *   @param List<ClerkDto>
     *   @param HttpServletResponse
     *   @throws IOException
     *   @throws RuntimeException
     * */
    @Override
    public void clerkExcelDownload(List<ClerkDto> data, HttpServletResponse response) {
        // 엑셀파일(Workbook) 객체 생성
        Workbook workbook =  getXSSFWorkBook();

        // 엑셀파일 sheet를 만들고, sheet의 이름을 지정해 줄 수 있다.
        Sheet sheet = workbook.createSheet("첫 번째 시트");

        // 엑셀의 열에 해당하는 Cell 객체 생성
        Cell cell = null;

        // 엑셀의 행에 해당하는 Row 객체 생성
        Row row = null;

        // List가 아닌 DTO를 넘겨줘야 하므로 메서드를 통해 DTO의 class 정보가 담긴 class 객체를 넣어준다.
        // Header의 내용을 List로 반환 받는다(엑셀의 Cell의 첫줄이 된다.)
        List<String> excelHeaderList = getHeaderName(getClass(data));

        // Header - 열의 첫줄(컬럼 이름들)을 그리는 작업

        // 첫 행을 생성해준다.
        row = sheet.createRow(0);

        // 헤더의 수(컬럼 이름의 수)만큼 반복해서 행을 생성한다.
        for(int i=0; i<excelHeaderList.size(); i++) {

            // 열을 만들어준다.
            cell = row.createCell(i);

            // 열에 헤더이름(컬럼 이름)을 넣어준다.
            cell.setCellValue(excelHeaderList.get(i));
        }

        // Body
        // 헤더 밑의 엑셀 파일 내용부분에 들어갈 내용을 그리는 작업
        renderClerkExcelBody(data, sheet, row, cell);

        // DownLoad
        // 엑셀 파일이 완성 되면 파일 다운로드를 위해 content-type과 Header를 설정해준다.
        // filename=파일이름.xlsx 가 파일의 이름이 된다.
        response.setContentType("ms-vnd/excel");
        response.setHeader("Content-Disposition", "attachment;filename=clerk.xlsx");

        try {
            // 엑셀 파일을 다운로드 하기 위해 write() 메서드를 사용한다.
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            // checked 예외를 사용하면 추후 의존이나 예외 누수 문제가 생길 수 있으므로
            // RuntimeException으로 한번 감싸서, cause가 나올 수 있게 발생한 예외를 넣어준다.
            log.error("Workbook write 수행 중 IOException 발생!");
            throw new RuntimeException(e);
        } finally {
            // 파일 입출력 스트림을 사용한 후에는 예외 발생 여부와 관계없이 반드시 닫아 주어야 한다.
            closeWorkBook(workbook);
        }
    }

    /*
     *   엑셀의 본문에 내용을 그려주는 로직
     *   @param List<ClerkDto>
     *   @param Sheet
     *   @param Row
     *   @param Cell
     * */
    @Override
    public void renderClerkExcelBody(List<ClerkDto> data, Sheet sheet, Row row, Cell cell) {
        // 현재 행의 개수를 가지고 있는 변수 rowCount 선언(Header를 그리고 시작했으므로 1부터 시작)
        int rowCount = 1;

        // 조회해온 데이터 리스트(List<ClerkDto>)의 크기만큼 반복문을 실행한다.
        for(ClerkDto clerk : data) {

            // 헤더를 설정할때 0번 인덱스가 사용 되었으므로, i값에 1을 더해서 1번 로우(행)부터 생성한다.
            row = sheet.createRow(rowCount++);

            // TODO : 하드코딩 대신 추후 동적으로 처리 할 수 있도록 개선 예정
            // 첫 번째 cell(열)을 생성한다.
            cell = row.createCell(0);
            // 첫 번째 cell(열)의 값을 셋팅한다.
            cell.setCellValue(clerk.getId());
            // 두 번째 cell(열)을 생성한다.
            cell = row.createCell(1);
            // 두 번째 cell(열)의 값을 셋팅한다.
            cell.setCellValue(clerk.getName());
            // 세 번째 cell(열)을 생성한다.
            cell = row.createCell(2);
            // 세 번째 cell(열)의 값을 셋팅한다.
            cell.setCellValue(clerk.getSalary());
            // 네 번째 cell(열)을 생성한다.
            cell = row.createCell(3);
            // 네 번째 cell(열)의 값을 셋팅한다.
            cell.setCellValue(clerk.getEmployDate());
        }
    }


    /*
     *  학생 엑셀을 읽어서 List로 반환해주는 로직
     *  @param MulitpartFile
     *  @return List<StudentDto>
     *  @throws IOException
     *  @throws IllegalStateException
     * */
    @Override
    public List<ClerkDto> readClerkExcel(MultipartFile file) {
        List<ClerkDto> clerkDtoList = new ArrayList<>();

        // 파일의 확장자만 바꿔서 올리는 FakeFile을 방지하기 위해 Apache Tika 사용
        Tika tika = getTika();

        // try ~ catch ~ finally 블럭에서 사용하기 위해 Workbook 초기화
        Workbook workbook = null;

        try {
            // Tika를 사용해서 MIME 타입을 얻어낸다
            String mimeType = tika.detect(file.getBytes());
            log.info("mimeType = {}", mimeType);


            // 파일의 확장자 명을 구한다
            // ex) Clerk.xlsx => return "xlsx"
            String extension = FilenameUtils.getExtension(file.getOriginalFilename());
            log.info("extension = {}", extension);

            // 파일의 확장자명과 MIME 타입으로 엑셀파일이 맞는지 검증한다.
            if(!isExcelPresent(mimeType, extension)) {
                throw new IllegalStateException("엑셀 파일이 아닙니다. 확인 후 다시 업로드 해주세요.");
            }

            // 가져온 파일로 Workbook 객체를 생성
            workbook = new XSSFWorkbook(file.getInputStream());

            // 현재 시트를 첫번째 시트만 사용중이므로 0번 인덱스 시트를 가져온다.
            Sheet sheet = workbook.getSheetAt(0);

            // 헤더를 제외하고 데이터를 뽑아야 하므로, 1번 인덱스 부터 시작한다.
            for(int i=1; i<sheet.getPhysicalNumberOfRows(); i++) {

                // 시트로부터 행을 가져온다.
                Row row = sheet.getRow(i);

                // Cell들의 값을 얻어와서 StudentDto와 매핑해준다.
                ClerkDto clerkDto = ClerkDto.builder()
                        .id((long)row.getCell(0).getNumericCellValue())
                        .name(row.getCell(1).getStringCellValue())
                        .salary((int)row.getCell(2).getNumericCellValue())
                        .employDate(row.getCell(3).getStringCellValue())
                        .build();

                // 반환할 studentDtoList에 추가한다.
                clerkDtoList.add(clerkDto);
            }
        } catch (IOException e) {
            // checked 예외를 사용하면 추후 의존이나 예외 누수 문제가 생길 수 있으므로
            // RuntimeException으로 한번 감싸서, cause가 나올 수 있게 발생한 예외를 넣어준다.
            log.error("엑셀 파일을 읽는 도중 IOException 발생!");
            throw new RuntimeException(e);
        } finally {
            closeWorkBook(workbook);
        }


        return clerkDtoList;
    }

    /*
     *   엑셀 헤더 이름들을 반환해주는 로직
     *   @param Class<?>
     *   @throws IllegalStateException
     * */
    private List<String> getHeaderName(Class<?> type) {

        // 스트림으로 엑셀 헤더 이름들을 리스트로 반환
        // 1. 매개변수로 전달된 클래스의 필드들을 배열로 받아, 스트림을 생성
        // 2. @ExcelColumn 애너테이션이 붙은 필드만 수집
        // 3. @ExcelColumn 애너테이션이 붙은 필드에서 애너테이션의 값을 매핑
        // 4. LinkedList로 반환
        List<String> excelHeaderNameList =  Arrays.stream(type.getDeclaredFields())
                .filter(s -> s.isAnnotationPresent(ExcelColumn.class))
                .map(s -> s.getAnnotation(ExcelColumn.class).headerName())
                .collect(Collectors.toCollection(LinkedList::new));

        // 헤더의 이름을 담은 List가 비어있을 경우, 헤더 이름이 지정되지 않은 것이므로, 예외를 발생시킨다.
        if(CollectionUtils.isEmpty(excelHeaderNameList)) {
            log.error("헤더 이름이 조회되지 않아 예외 발생!");
            throw new IllegalStateException("헤더 이름이 없습니다.");
        }

        return excelHeaderNameList;
    }


    /*
    *   List(데이터 리스트)에 담긴 DTO의 클래스 정보를 반환하는 메서드
    *   @param List<?>
    *   @return Class<?>
    * */
    private Class<?> getClass(List<?> data) {
        // List가 비어있지 않다면 List가 가지고 있는 모든 DTO는 같은 필드를 가지고 있으므로,
        // 맨 마지막 DTO만 빼서 클래스 정보를 반환한다.
        if(!CollectionUtils.isEmpty(data)) {
            return data.get(data.size()-1).getClass();
        } else {
            log.error("리스트가 비어 있어서 예외 발생!");
            throw new IllegalStateException("조회된 리스트가 비어 있습니다. 확인 후 다시 진행해주시기 바랍니다.");
        }
    }


    /*
    *   엑셀 파일인지 체크
    *   @param String mime
    *   @param String extension
    *   @return boolean
    * */
    private boolean isExcelPresent(String mime, String extension) {
        // Microsoft Office 파일의 MIME는 application/x-tika-ooxml이다.
        return mime.equals("application/x-tika-ooxml") &&
                // 확장자는 xlsx이어야 한다.
                extension.equals("xlsx");
    }

    /*
    *  WorkBook의 스트림을 닫아주는 로직
    *  @param Workbook
    *  @throws RuntimeException
    *  @throws IOException
    * */
    private void closeWorkBook(Workbook workbook) {
        try {
            if(workbook != null) {
                workbook.close();
            }
        } catch (IOException e) {
            // checked 예외를 사용하면 추후 의존이나 예외 누수 문제가 생길 수 있으므로
            // RuntimeException으로 한번 감싸서, cause가 나올 수 있게 발생한 예외를 넣어준다.
            throw new RuntimeException(e);
        }
    }

    /*
    *   XSSFWorkbook을 반환하는 로직
    *   @return XSSFWorkbook
    * */
    private XSSFWorkbook getXSSFWorkBook() {
        return new XSSFWorkbook();
    }


    /*
    *   Tika를 반환하는 로직
    *   @return Tika
    * */
    private Tika getTika() {
        return new Tika();
    }

}

