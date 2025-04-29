package com.SNC.email.Service;

import com.SNC.register.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailSendService {

    private final JavaMailSender mailSender; // Spring Boot의 JavaMailSender는 application.yml의 설정을 자동으로 읽어옴
    private final MemberMapper memberMapper; // 회원 정보에서 이메일 추출

    public void sendEmail(String mbrCd, String message) {
        String email = memberMapper.findEmailByMember(mbrCd); // 특정 회원의 이메일 찾기

        if (email == null || email.isEmpty()) { // 이메일이 null 이거나 비어있다면 return
            log.error("member email Info null : {}", mbrCd);
            return;
        }

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email); // 특정 회원의 이메일
        mailMessage.setSubject("📢 SNC 시세 알림 서비스 도착!"); // 메일 제목
        mailMessage.setText(message); // 파라미터로 받아온 메세지 내용

        try {
            mailSender.send(mailMessage); // 성공시 메일 발송
            log.info("✅ email send success : {}", email);
        } catch (Exception e) { // 실패시 에러 처리
            log.error("email send fail : {}", e.getMessage());
        }
    }
}
