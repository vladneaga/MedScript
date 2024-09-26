package group11.medScriptAPI;

import group11.medScriptAPI.entity.*;
import group11.medScriptAPI.repository.InsuranceRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.Instant;
import java.util.Date;

@SpringBootApplication
public class MedScriptApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(MedScriptApiApplication.class, args);
	}


}
