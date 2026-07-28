package com.datn.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class ProjectApplication {

	public static void main(String[] args) {
		System.setProperty("mail.mime.charset", "UTF-8");
		SpringApplication.run(ProjectApplication.class, args);
	}

}
