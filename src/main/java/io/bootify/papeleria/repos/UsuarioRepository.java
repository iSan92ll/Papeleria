package io.bootify.papeleria.repos;

import io.bootify.papeleria.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    boolean existsByCorreoIgnoreCase(String correo);

}
