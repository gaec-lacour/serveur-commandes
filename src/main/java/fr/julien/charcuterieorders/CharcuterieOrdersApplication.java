package fr.julien.charcuterieorders;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class CharcuterieOrdersApplication {

	public static void main(String[] args) {

		TimeZone.setDefault(TimeZone.getTimeZone("Europe/Paris"));
		SpringApplication.run(CharcuterieOrdersApplication.class, args);
	}

}
