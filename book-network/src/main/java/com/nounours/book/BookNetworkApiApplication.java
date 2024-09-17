package com.nounours.book;

import com.nounours.book.role.Role;
import com.nounours.book.role.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware") // the name of the method which treats about the "bean"
@EnableAsync

public class BookNetworkApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookNetworkApiApplication.class, args);
	}

	// To initialize (insert a user role into the table role) the roles of users
	@Bean
	public CommandLineRunner runner(RoleRepository roleRepository){
		return args ->{

			if (roleRepository.findByName("USER").isEmpty()){
				roleRepository.save(
						Role.builder().name("USER").build()

				);
			}

		};
	};
}
