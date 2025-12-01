package com.niagapulse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.niagapulse")
public class NiagapulseApplication {

	public static void main(String[] args) {
		SpringApplication.run(NiagapulseApplication.class, args);
	}

}
