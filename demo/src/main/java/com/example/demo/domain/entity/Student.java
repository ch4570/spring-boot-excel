package com.example.demo.domain.entity;

import com.example.demo.annotation.ExcelColumn;
import com.example.demo.domain.dto.StudentDto;
import lombok.*;

import javax.persistence.*;

@Entity @Getter @Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Student {
    
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BAN")
    private Long id;
    
    @Column(name = "NAME")
    private String name;


    // Student Entity를 StudentDto로 변경해 반환하는 메서드
    public StudentDto toDto() {
        return new StudentDto(id, name);
    }
}
