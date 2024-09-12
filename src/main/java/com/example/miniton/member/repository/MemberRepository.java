package com.example.miniton.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import  com.example.miniton.member.domain.Member;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<com.example.miniton.member.domain.Member> findByEmail(String email);
}
