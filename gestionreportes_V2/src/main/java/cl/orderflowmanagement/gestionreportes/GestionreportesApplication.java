package cl.orderflowmanagement.gestionreportes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients 
public class GestionreportesApplication {

    public static void main(String[] args) {
        SpringApplication.run(GestionreportesApplication.class, args);
        System.out.println("arranco XD");
    }

}