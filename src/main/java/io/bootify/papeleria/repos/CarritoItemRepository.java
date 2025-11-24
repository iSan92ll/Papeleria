package io.bootify.papeleria.repos;

import io.bootify.papeleria.domain.CarritoItem;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CarritoItemRepository extends JpaRepository<CarritoItem, Long> {

    CarritoItem findFirstByCarritoIdCarrito(Long idCarrito);

    CarritoItem findFirstByProductoIdProducto(Long idProducto);

}
