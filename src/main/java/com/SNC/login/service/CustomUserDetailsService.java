package com.SNC.login.service;

import com.SNC.login.model.CustomUserDetails;
import com.SNC.register.dto.MemberDto;
import com.SNC.register.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService  implements UserDetailsService {

    private final MemberMapper memberMapper;

    /**
     * @param userId
     * @return UserDetails
     * @Description uesrId로 UserDetail객체 반환
     */
    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        log.info("검증시작:{}",userId);
        MemberDto user = memberMapper.getByUserId(userId);
        if (user == null) {
            log.info("해당 사용자 미존재");
            throw new UsernameNotFoundException("User not found with email: " + userId);
        }
        return new CustomUserDetails(
                user.getMbrCd(),
                user.getMbrId(),
                user.getMbrPass(),
                user.getMbrNickName(), // ✅ nickname 추가
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

}
