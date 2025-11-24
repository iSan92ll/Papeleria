package io.bootify.papeleria.repos;

import io.bootify.papeleria.domain.VentaItem;
import org.springframework.data.jpa.repository.JpaRepository;


public interface VentaItemRepository extends JpaRepository<VentaItem, Long> {

    VentaItem findFirstByVentaIdVenta(Long idVenta);

    VentaItem findFirstByProductoIdProducto(Long idProducto);

}
