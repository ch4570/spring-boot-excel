package com.example.demo.service;

import com.example.demo.domain.entity.CompareStudent;
import com.example.demo.repository.CompareStudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CompareStudentService {

    private final CompareStudentRepository compareRepository;
    private final EntityManager em;

    /*
     *   차이 나는 데이터 Bulk-insert
     *   @param Student
     *   @return Student
     * */
    @Transactional
    public List<CompareStudent> saveAllCompareStudent(List<CompareStudent> compareStudent) {
        return compareRepository.saveAll(compareStudent);
    }

    /*
     *   차이나는 데이터 전부 삭제
     *   @return
     * */
    @Transactional
    public void removeAllCompareData() {
        compareRepository.deleteAll();
    }


    /*
     *   차이 나는 데이터 List 조회
     *   @return List<CompareStudent>
     * */
    @Transactional(readOnly = true) // 플래시가 작동하지 않아서 성능 향상
    public List<CompareStudent> findAllCompareStudent() {

        // 학생 전체 조회 - 스칼라 타입 조회로 성능 최적화
        List<CompareStudent> compareList = em.createQuery("select c from CompareStudent c")
                .setHint("org.hibernate.readOnly", true)
                .getResultList();

        // 학생 전체 조회 후 리스트가 비어있다면, 예외를 던진다.
        if(CollectionUtils.isEmpty(compareList)) {
            log.error("조회된 학생이 없어서 예외 발생!");
            throw new IllegalStateException("조회된 학생 데이터가 없습니다. 확인 후 다시 진행해주시기 바랍니다.");
        }

        return compareList;
    }
}
