package net.samitkumar.springboot_crud_api;

import org.springframework.boot.SpringApplication;

public class TestSpringbootCrudApiApplication {

	public static void main(String[] args) {
		SpringApplication.from(SpringbootCrudApiApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
