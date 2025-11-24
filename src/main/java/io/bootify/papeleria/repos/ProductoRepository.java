package io.bootify.papeleria.repos;

import io.bootify.papeleria.domain.Producto;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ProductoRepository extends JpaRepository<Producto, Long> {
}
