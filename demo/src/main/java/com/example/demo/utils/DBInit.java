package com.example.demo.utils;

import com.example.demo.domain.dto.ClerkDto;
import com.example.demo.domain.dto.StudentDto;
import com.example.demo.domain.entity.Clerk;
import com.example.demo.domain.entity.Student;
import com.example.demo.repository.ClerkRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.service.ClerkService;
import com.example.demo.service.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DBInit {

    private final StudentService studentService;
    private final StudentRepository studentRepository;
    private final ClerkRepository clerkRepository;
    private final ClerkService clerkService;

    // Bean이 초기화 될때 한 번 실행되는 메서드
    @PostConstruct
    private void init() {

        // 있는 데이터를 전부 삭제 - 학생 & 직원
        studentRepository.deleteAll();
        clerkRepository.deleteAll();

        // 30명의 학생 & 사원을 insert
        insertDummyStudent();
        insertDummyClerk();

        // 학생 & 사원 리스트를 조회
        List<Student> list = studentService.findAllStudent();
        List<Clerk> clerkList = clerkService.findAllClerk();



        // 찾은 학생을 DTO로 변환 후 log로 출력
        for(Student s : list) {
            StudentDto studentDto = s.toDto();
            log.info("찾은 학생 = {}", studentDto);
        }

        // 찾은 사원을 DTO로 변환 후 log로 출력
        for(Clerk c : clerkList) {
            ClerkDto clerkDto = c.toDto();
            log.info("찾은 사원 = {}", clerkDto);
        }
    }

    private void insertDummyStudent() {
        for(int i=1; i<100000; i++) {
            studentService.saveStudent(Student.builder().name("학생"+i).build());
        }
    }

    private void insertDummyClerk() {
        for(int i=1; i<30; i++) {
            clerkService.saveClerk(Clerk.builder().name("사원"+i).salary(1000000 * i).build());
        }
    }
}
