package io.bootify.papeleria.rest;

import io.bootify.papeleria.model.ProductoDTO;
import io.bootify.papeleria.service.ProductoService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api/productos", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProductoResource {

    private final ProductoService productoService;

    public ProductoResource(final ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public ResponseEntity<List<ProductoDTO>> getAllProductos() {
        return ResponseEntity.ok(productoService.findAll());
    }

    @GetMapping("/{idProducto}")
    public ResponseEntity<ProductoDTO> getProducto(
            @PathVariable(name = "idProducto") final Long idProducto) {
        return ResponseEntity.ok(productoService.get(idProducto));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createProducto(@RequestBody @Valid final ProductoDTO productoDTO) {
        final Long createdIdProducto = productoService.create(productoDTO);
        return new ResponseEntity<>(createdIdProducto, HttpStatus.CREATED);
    }

    @PutMapping("/{idProducto}")
    public ResponseEntity<Long> updateProducto(
            @PathVariable(name = "idProducto") final Long idProducto,
            @RequestBody @Valid final ProductoDTO productoDTO) {
        productoService.update(idProducto, productoDTO);
        return ResponseEntity.ok(idProducto);
    }

    @DeleteMapping("/{idProducto}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteProducto(
            @PathVariable(name = "idProducto") final Long idProducto) {
        productoService.delete(idProducto);
        return ResponseEntity.noContent().build();
    }

}
