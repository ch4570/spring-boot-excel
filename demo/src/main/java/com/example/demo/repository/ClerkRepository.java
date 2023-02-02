package com.example.demo.repository;

import com.example.demo.domain.entity.Clerk;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClerkRepository extends JpaRepository<Clerk, Long> {

}
