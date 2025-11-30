package io.bootify.papeleria.repos;

import io.bootify.papeleria.domain.Usuario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    boolean existsByCorreoIgnoreCase(String correo);

    // Método que usa el servicio para buscar por correo (retorna Optional)
    Optional<Usuario> findByCorreo(String correo);

    // Otra variante por si la necesitas
    Optional<Usuario> findByCorreoIgnoreCase(String correo);
}
