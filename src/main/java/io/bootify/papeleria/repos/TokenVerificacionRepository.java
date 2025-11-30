package io.bootify.papeleria.repos;

import io.bootify.papeleria.domain.TokenVerificacion;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TokenVerificacionRepository extends JpaRepository<TokenVerificacion, Long> {

    TokenVerificacion findFirstByUsuarioIdUsuario(Integer idUsuario);

    // Métodos necesarios para la lógica de verificación/recuperación
    Optional<TokenVerificacion> findByToken(String token);

    // Opcional: buscar el último token por usuario
    Optional<TokenVerificacion> findFirstByUsuarioIdUsuarioOrderByDateCreatedDesc(Integer idUsuario);
}
