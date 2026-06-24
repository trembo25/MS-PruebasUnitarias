package cl.orderflowmanagement.gestionusuarios.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.orderflowmanagement.gestionusuarios.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer>{

    Optional<Usuario> findByCorreoAndContrasenia(String correo, String contrasenia);
}
