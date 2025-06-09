package academy.prog.julia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The entry point for the Spring Boot application.
 *
 * This class is responsible for launching the Spring Boot application. It uses
 * the @SpringBootApplication annotation, which enables auto-configuration, component
 * scanning, and configuration properties.
 */
@SpringBootApplication
public class ProgJuliaApplication {

	/**
	 * The main method that serves as the entry point for the Spring Boot application.
	 *
	 * @param args command-line arguments passed to the application.
	 */
	public static void main(String[] args) {
		SpringApplication.run(ProgJuliaApplication.class, args);
	}

}
