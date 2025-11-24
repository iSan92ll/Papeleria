package io.bootify.papeleria.repos;

import io.bootify.papeleria.domain.Venta;
import org.springframework.data.jpa.repository.JpaRepository;


public interface VentaRepository extends JpaRepository<Venta, Long> {

    Venta findFirstByUsuarioIdUsuario(Integer idUsuario);

    Venta findFirstByDireccionIdDireccion(Integer idDireccion);

}
