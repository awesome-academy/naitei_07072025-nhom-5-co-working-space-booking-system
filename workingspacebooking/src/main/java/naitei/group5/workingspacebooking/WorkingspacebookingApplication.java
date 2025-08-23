package naitei.group5.workingspacebooking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableScheduling
@SpringBootApplication
public class WorkingspacebookingApplication {

	public static void main(String[] args) {
		SpringApplication.run(WorkingspacebookingApplication.class, args);
	}

}
