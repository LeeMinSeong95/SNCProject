package com.SNC.register.service;

import com.SNC.register.mapper.MemberMapper;
import com.SNC.register.dto.MemberDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {
    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * @param memberDto
     * @return
     * @Description 회원정보 검증 및 회원가입 처리
     */
    @Transactional
    public void registerUser(MemberDto memberDto) {
        if(existsById(memberDto.getMbrId())){
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }
        if(existsByEmail(memberDto.getMbrEmail())){
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        if(existsByNickName(memberDto.getMbrNickName())){
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }
        memberDto.setMbrImg(" ");
        memberDto.setMbrPass(passwordEncoder.encode(memberDto.getMbrPass()));
        memberMapper.memberInsert(memberDto);
    }

    public boolean existsByEmail(String email) { return memberMapper.findByEmail(email) != null; }

    public boolean existsById(String memberId) { return memberMapper.findById(memberId) != null; }

    public boolean existsByNickName(String mbrNickName) { return memberMapper.findByNickName(mbrNickName) != null; }

}