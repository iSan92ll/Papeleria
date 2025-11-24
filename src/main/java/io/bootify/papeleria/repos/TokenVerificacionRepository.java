package io.bootify.papeleria.repos;

import io.bootify.papeleria.domain.TokenVerificacion;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TokenVerificacionRepository extends JpaRepository<TokenVerificacion, Long> {

    TokenVerificacion findFirstByUsuarioIdUsuario(Integer idUsuario);

}
