package com.github.aquilesdias.libraryapi;

import com.github.aquilesdias.libraryapi.service.EmailService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@EnableScheduling
public class LibraryApiApplication {

	@Autowired
	private EmailService emailService;

	@Bean
	public ModelMapper modelmapper(){
		return new ModelMapper();
	}

	public CommandLineRunner commandLineRunner () {
		return args -> {
			List<String> mails = Arrays.asList("email@inbox.mailtrap.io");
			emailService.sendMails("Teste de emails", mails);
			System.out.println("Email(s) enviados!");
		};
	}

	public static void main(String[] args) {
		SpringApplication.run(LibraryApiApplication.class, args);
	}

}
