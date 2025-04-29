package com.SNC.visit.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Service
public class RedisService {
    private final StringRedisTemplate redisTemplate;
    private static final String TOTAL_VISITOR_KEY = "totalVisitors";
    private static final String TODAY_VISITOR_KEY = "todayVisitors";
    private static final String ACTIVE_USERS_KEY = "activeUsers";

    public RedisService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // 전체 방문자 수 조회
    public long getTotalVisitors() {
        String value = redisTemplate.opsForValue().get(TOTAL_VISITOR_KEY);
        return value == null ? 0 : Long.parseLong(value);
    }

    // 오늘 방문자 수 조회
    public long getTodayVisitors() {
        String todayKey = TODAY_VISITOR_KEY + ":" + LocalDate.now();
        String value = redisTemplate.opsForValue().get(todayKey);
        return value == null ? 0 : Long.parseLong(value);
    }

    // 현재 접속자 수 조회
    public long getActiveUsers() {
        String value = redisTemplate.opsForValue().get(ACTIVE_USERS_KEY);
        return value == null ? 0 : Long.parseLong(value);
    }

    // 현재 접속자 수 증가
    public void incrementActiveUsers() {
        redisTemplate.opsForValue().increment(ACTIVE_USERS_KEY, 1);
    }

    // 현재 접속자 수 감소
    public void decrementActiveUsers() {
        redisTemplate.opsForValue().decrement(ACTIVE_USERS_KEY, 1);
    }
}
