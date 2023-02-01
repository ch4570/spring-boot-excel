package com.example.demo.utils;

import com.example.demo.domain.dto.StudentDto;
import com.example.demo.domain.entity.Student;
import com.example.demo.repository.StudentRepository;
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
    private final StudentRepository repository;

    // Bean이 초기화 될때 한 번 실행되는 메서드
    @PostConstruct
    public void init() {

        // 있는 데이터를 전부 삭제
        repository.deleteAll();

        // 30명의 학생을 insert
        insertDummyStudent();

        // 학생 리스트를 조회
        List<Student> list = studentService.findAllStudent();

        // 찾은 학생을 DTO로 변환 후 log로 출력
        for(Student s : list) {
            StudentDto dto = s.toDto();
            log.info("찾은 학생 = {}", dto);
        }
    }

    public void insertDummyStudent() {
        for(int i=1; i<30; i++) {
            studentService.saveStudent(Student.builder().name("학생"+i).build());
        }
    }
}
