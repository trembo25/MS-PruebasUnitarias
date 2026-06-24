package cl.duoc.gestionreservas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class GestionreservasApplication {

	public static void main(String[] args) {
		SpringApplication.run(GestionreservasApplication.class, args);
	}

}
