package com.example.demo.repository;

import com.example.demo.domain.entity.CompareStudent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompareStudentRepository extends JpaRepository<CompareStudent, Long> {
}
