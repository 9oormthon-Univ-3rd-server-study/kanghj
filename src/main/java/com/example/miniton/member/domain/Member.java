package com.example.miniton.member.domain;

import com.example.miniton.common.BaseTimeEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name= "member")
public class Member extends BaseTimeEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "member_name",nullable = false)
    private String name;

    @Column(name = "email",nullable = false, unique = true)
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "member_password",nullable = false)
    private String password;

    @Column(name ="nick_name",nullable = false)
    private String nickName;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles = new ArrayList<>();

    @Builder
    protected Member(String name, String email, String password, String nickName, List<String> roles){
        this.name = name;
        this.email = email;
        this.password = password;
        this.nickName = nickName;
        this.roles = roles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    // 사용자의 email 반환하는 메서드,
    //일반적으로 외부에 노출되어도 되는 정보이기 때문에 JsonProperty.Access.WRITE_ONLY 가 필요하지 않다.
    @Override
    public String getUsername() {
        return this.email;
    }

    //사용자 계정의 만료 여부
    //false를 반환하면 사용자 계정이 만료되었다는 뜻
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    //사용자 계정이 잠겨있는지 여부
    //false를 반환하면 계정이 잠겨있다는 뜻
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    //사용자의 자격증명(패스워드 등) 만료 여부
    //false를 반환하면 자격 증명이 만료되었다는 뜻
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    //사용자의 계정 활성화 여부
    //false를 반환하면 계정이 비활성화 되었다는 뜻
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public boolean isEnabled() {
        return true;
    }
}
