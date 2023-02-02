package com.example.demo.domain.entity;

import com.example.demo.domain.dto.ClerkDto;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity @Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@EntityListeners(AuditingEntityListener.class)
public class Clerk {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Integer salary;

    private String employDate;

    // Clerk 엔티티가 Persist 될때 입사일(employDate)를 자동 셋팅 해주는 메서드
    @PrePersist
    void addEmployDate() {
        this.employDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    // Clerk 엔티티를 ClerkDTO로 변환하는 메서드
    public ClerkDto toDto() {
        return new ClerkDto(id,name,salary,employDate);
    }

}
