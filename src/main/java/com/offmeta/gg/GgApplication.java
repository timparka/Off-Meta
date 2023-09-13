package com.offmeta.gg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GgApplication {

	public static void main(String[] args) {
		SpringApplication.run(GgApplication.class, args);
	}

}
