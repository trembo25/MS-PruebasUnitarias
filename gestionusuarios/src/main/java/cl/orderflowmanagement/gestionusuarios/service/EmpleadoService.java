package cl.orderflowmanagement.gestionusuarios.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.orderflowmanagement.gestionusuarios.model.Empleado;
import cl.orderflowmanagement.gestionusuarios.model.RolEmpleado;
import cl.orderflowmanagement.gestionusuarios.model.Usuario;
import cl.orderflowmanagement.gestionusuarios.repository.EmpleadoRepository;
import cl.orderflowmanagement.gestionusuarios.repository.RolEmpleadoRepository;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class EmpleadoService {

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private RolEmpleadoRepository rolEmpleadoRepository;

    public List<Empleado> getAll() {
        return empleadoRepository.findAll();
    }

    public Empleado addEmpleado(Empleado empleado) {
        return empleadoRepository.save(empleado);
    }

    public void deleteEmpleado(Integer id) {
        empleadoRepository.deleteById(id);
    }

    public Empleado updateEmpleado(Empleado empleado) {
        Empleado empleadoAntiguo = empleadoRepository.findById(empleado.getId()).orElse(null);

        if (empleadoAntiguo != null) {
            empleadoAntiguo.setNombres(empleado.getNombres());
            empleadoAntiguo.setApellidos(empleado.getApellidos());
            empleadoAntiguo.setFechaNacimiento(empleado.getFechaNacimiento());
            empleadoAntiguo.setFechaContratacion(empleado.getFechaContratacion());
            empleadoAntiguo.setSueldoBase(empleado.getSueldoBase());
            empleadoAntiguo.setDireccion(empleado.getDireccion());

            if (empleado.getRolEmpleado() != null) {
                RolEmpleado nuevoRol = rolEmpleadoRepository.findById(empleado.getRolEmpleado().getId()).orElse(null);
                empleadoAntiguo.setRolEmpleado(nuevoRol);
            }

            if (empleado.getUsuario() != null && empleadoAntiguo.getUsuario() != null) {
                Usuario usuarioAntiguo = empleadoAntiguo.getUsuario();
                Usuario usuarioNuevo = empleado.getUsuario();

                usuarioAntiguo.setCorreo(usuarioNuevo.getCorreo());
                usuarioAntiguo.setContrasenia(usuarioNuevo.getContrasenia());
            }

            return empleadoRepository.save(empleadoAntiguo);
        }

        return null;
    }

    public Empleado findEmpleadoById(Integer id) {
        return empleadoRepository.findById(id).orElse(null);
    }

}
