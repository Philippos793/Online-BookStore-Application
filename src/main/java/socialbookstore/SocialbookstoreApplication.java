package socialbookstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SocialbookstoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(SocialbookstoreApplication.class, args);
	}

}
