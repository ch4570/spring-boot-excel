package com.example.demo.compare;


import com.example.demo.domain.dto.StudentDto;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CompareTest {

    @Test
    void isEquals() {
        List<StudentDto> dtoList1 = new ArrayList<>();
        List<StudentDto> dtoList2 = new ArrayList<>();

        for(int i=0; i<5; i++) {
            StudentDto studentDto = StudentDto.builder()
                    .ban(Long.valueOf(i))
                    .name("홍길동"+i)
                    .build();
            dtoList1.add(studentDto);
        }

        for(int i=0; i<5; i++) {
            StudentDto studentDto = StudentDto.builder()
                    .ban(Long.valueOf(i))
                    .name("홍길동"+i)
                    .build();
            dtoList2.add(studentDto);
        }

        dtoList2.get(4).setBan(7L);

//        for(int i=0; i<dtoList1.size(); i++) {
//            System.out.println("dtoList1 = " + dtoList1.get(i));
//            System.out.println("dtoList2 = " + dtoList2.get(i));
//            System.out.println("dtoList1 equals dtoList2 ? = " + dtoList1.get(i).equals(dtoList2.get(i)));
//        }

        List<StudentDto> notMatchList = dtoList1.stream()
                .filter(list -> dtoList2.stream()
                        .noneMatch(Predicate.isEqual(list))).collect(Collectors.toList());


    }
}
