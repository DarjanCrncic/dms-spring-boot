package com.example.dms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class DmsApplication {

	public static void main(String[] args) {
		SpringApplication.run(DmsApplication.class, args);
	}

}
