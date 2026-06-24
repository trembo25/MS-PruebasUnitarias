package cl.orderflowmanagement.gestionpedidos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class GestionpedidosApplication {

	public static void main(String[] args) {
		SpringApplication.run(GestionpedidosApplication.class, args);
	}

}
