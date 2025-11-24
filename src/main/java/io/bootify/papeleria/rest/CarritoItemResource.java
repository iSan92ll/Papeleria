package io.bootify.papeleria.rest;

import io.bootify.papeleria.model.CarritoItemDTO;
import io.bootify.papeleria.service.CarritoItemService;
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
@RequestMapping(value = "/api/carritoItems", produces = MediaType.APPLICATION_JSON_VALUE)
public class CarritoItemResource {

    private final CarritoItemService carritoItemService;

    public CarritoItemResource(final CarritoItemService carritoItemService) {
        this.carritoItemService = carritoItemService;
    }

    @GetMapping
    public ResponseEntity<List<CarritoItemDTO>> getAllCarritoItems() {
        return ResponseEntity.ok(carritoItemService.findAll());
    }

    @GetMapping("/{idCarritoItem}")
    public ResponseEntity<CarritoItemDTO> getCarritoItem(
            @PathVariable(name = "idCarritoItem") final Long idCarritoItem) {
        return ResponseEntity.ok(carritoItemService.get(idCarritoItem));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createCarritoItem(
            @RequestBody @Valid final CarritoItemDTO carritoItemDTO) {
        final Long createdIdCarritoItem = carritoItemService.create(carritoItemDTO);
        return new ResponseEntity<>(createdIdCarritoItem, HttpStatus.CREATED);
    }

    @PutMapping("/{idCarritoItem}")
    public ResponseEntity<Long> updateCarritoItem(
            @PathVariable(name = "idCarritoItem") final Long idCarritoItem,
            @RequestBody @Valid final CarritoItemDTO carritoItemDTO) {
        carritoItemService.update(idCarritoItem, carritoItemDTO);
        return ResponseEntity.ok(idCarritoItem);
    }

    @DeleteMapping("/{idCarritoItem}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteCarritoItem(
            @PathVariable(name = "idCarritoItem") final Long idCarritoItem) {
        carritoItemService.delete(idCarritoItem);
        return ResponseEntity.noContent().build();
    }

}
