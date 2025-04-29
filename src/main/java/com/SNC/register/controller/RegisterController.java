package com.SNC.register.controller;

import com.SNC.register.dto.MemberDto;
import com.SNC.register.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/register")
@Slf4j
public class RegisterController {
    private final MemberService memberService;

    @GetMapping("/join")
    public String join(HttpServletRequest request) {
        return "/member/join";
    }

    /**
     * @param member
     * @return String
     * @Description 회원가입
     */
    @PostMapping
    public String register(@ModelAttribute MemberDto member,Model model) {

        try{
            memberService.registerUser(member);
            model.addAttribute("successMessage", "회원가입이 성공적으로 완료되었습니다!");

        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "member/join";

        } catch(Exception e) {
            model.addAttribute("errorMessage", "회원가입중 오류가 발생했습니다");
            return "member/join";

        }

        return "member/login";
    }


}
