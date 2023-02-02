package com.example.demo.service;

import com.example.demo.domain.entity.Student;
import com.example.demo.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;

    /*
    *   학생 한명 저장
    *   @param Student
    *   @return Student
    * */
    @Transactional
    public Student saveStudent(Student student) {
        return studentRepository.save(student);
    }

    /*
    *   학생 전부 조회
    *   @return List<Student>
    * */
    @Transactional(readOnly = true)
    public List<Student> findAllStudent() {

        // 학생 전체 조회
        List<Student> studentList = studentRepository.findAll();

        // 학생 전체 조회 후 리스트가 비어있다면, 예외를 던진다.
        if(CollectionUtils.isEmpty(studentList)) {
            log.error("조회된 학생이 없어서 예외 발생!");
            throw new IllegalStateException("조회된 학생 데이터가 없습니다. 확인 후 다시 진행해주시기 바랍니다.");
        }

        return studentList;
    }

}
