package com.SNC.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**").permitAll()
                        .requestMatchers("/register/**", "/login/home",
                                "/request/token","/main/home",
                                "/main/**",
                                "/coin/start","/stock/**",
                                "/ws/**", "/images/**",
                                "/board/**","/comments/**",
                                "/likes/**", "/candles/month",
                                "/candle", "/jsonCheck",
                                "/news","/allCoinNews",
                                "/candles/**","/candle/**",
                                "/jsonCheck","/news",
                                "/bookmarks","/init/**",
                                "/favorite/**","/itemdetail/**"
                                ,"/volumeRank/**","/top5/**",
                                "/api/top5/**","/kakao/**",
                                "email/**", "alert/**")
                        .permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .maximumSessions(1) // 동시 로그인 개수 제한
                )
                .formLogin(login -> login
                        .loginPage("/login/home")
                        .defaultSuccessUrl("/main/home", true)
                        .failureUrl("/login/home?error=true") // 로그인 실패 시
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/main/home")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}