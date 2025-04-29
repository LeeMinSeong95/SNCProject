package com.SNC;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@MapperScan({"com.SNC.register.mapper", "com.SNC.board.mapper" , "com.SNC.coinFavorites.mapper",
			 "com.SNC.itemdetail.mapper", "com.SNC.alert.mapper", "com.SNC.bookMark.mapper" })

public class SncApplication {

	public static void main(String[] args) {
		SpringApplication.run(SncApplication.class, args);

	}
}
