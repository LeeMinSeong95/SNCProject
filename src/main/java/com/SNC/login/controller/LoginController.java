package com.SNC.login.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor // 생성자 자동 생성
@RequestMapping("/login")
public class LoginController {

    @GetMapping("/home")
    public String home() {
        return "member/login";
    }



}
