package io.bootify.papeleria.repos;

import io.bootify.papeleria.domain.Carrito;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CarritoRepository extends JpaRepository<Carrito, Long> {

    Carrito findFirstByUsuarioIdUsuario(Integer idUsuario);

}
