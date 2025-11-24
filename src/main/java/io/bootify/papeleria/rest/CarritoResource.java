package io.bootify.papeleria.rest;

import io.bootify.papeleria.model.CarritoDTO;
import io.bootify.papeleria.service.CarritoService;
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
@RequestMapping(value = "/api/carritos", produces = MediaType.APPLICATION_JSON_VALUE)
public class CarritoResource {

    private final CarritoService carritoService;

    public CarritoResource(final CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    @GetMapping
    public ResponseEntity<List<CarritoDTO>> getAllCarritos() {
        return ResponseEntity.ok(carritoService.findAll());
    }

    @GetMapping("/{idCarrito}")
    public ResponseEntity<CarritoDTO> getCarrito(
            @PathVariable(name = "idCarrito") final Long idCarrito) {
        return ResponseEntity.ok(carritoService.get(idCarrito));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createCarrito(@RequestBody @Valid final CarritoDTO carritoDTO) {
        final Long createdIdCarrito = carritoService.create(carritoDTO);
        return new ResponseEntity<>(createdIdCarrito, HttpStatus.CREATED);
    }

    @PutMapping("/{idCarrito}")
    public ResponseEntity<Long> updateCarrito(
            @PathVariable(name = "idCarrito") final Long idCarrito,
            @RequestBody @Valid final CarritoDTO carritoDTO) {
        carritoService.update(idCarrito, carritoDTO);
        return ResponseEntity.ok(idCarrito);
    }

    @DeleteMapping("/{idCarrito}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteCarrito(
            @PathVariable(name = "idCarrito") final Long idCarrito) {
        carritoService.delete(idCarrito);
        return ResponseEntity.noContent().build();
    }

}
