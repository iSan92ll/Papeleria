package io.bootify.papeleria.repos;

import io.bootify.papeleria.domain.Direccion;
import org.springframework.data.jpa.repository.JpaRepository;


public interface DireccionRepository extends JpaRepository<Direccion, Integer> {

    Direccion findFirstByUsuarioIdUsuario(Integer idUsuario);

}
