package com.example.demo.service;

import com.example.demo.domain.entity.Student;
import com.example.demo.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
        return studentRepository.findAll();
    }

}
