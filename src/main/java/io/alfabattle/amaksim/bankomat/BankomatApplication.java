package io.alfabattle.amaksim.bankomat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class BankomatApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankomatApplication.class, args);
	}

}
