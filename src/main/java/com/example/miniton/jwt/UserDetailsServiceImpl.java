package com.example.miniton.jwt;

import com.example.miniton.exception.CustomException;
import com.example.miniton.exception.ExceptionCode;
import com.example.miniton.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("[loadUserByUsername] loadUserByUsername 수행. username(email): {}", email);
        return (UserDetails) memberRepository.findByEmail(email).orElseThrow(()-> new CustomException(ExceptionCode.MEMBER_NOT_EXIST));
    }
}