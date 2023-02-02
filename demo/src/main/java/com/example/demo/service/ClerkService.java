package com.example.demo.service;

import com.example.demo.domain.entity.Clerk;
import com.example.demo.repository.ClerkRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClerkService {

    private final ClerkRepository clerkRepository;

    /*
     *   사원 한명 저장
     *   @param Clerk
     *   @return Clerk
     * */
    @Transactional
    public Clerk saveClerk(Clerk clerk) {
        return clerkRepository.save(clerk);
    }

    /*
     *   사원 전부 조회
     *   @return List<Clerk>
     * */
    @Transactional(readOnly = true)
    public List<Clerk> findAllClerk() {

        // 사원 전체 조회
        List<Clerk> clerkList = clerkRepository.findAll();

        // 사원 전체 조회 후 리스트가 비어있다면, 예외를 던진다.
        if(CollectionUtils.isEmpty(clerkList)) {
            log.error("조회된 사원이 없어서 예외 발생!");
            throw new IllegalStateException("조회된 사원 데이터가 없습니다. 확인 후 다시 진행해주시기 바랍니다.");
        }

        return clerkList;
    }
}
