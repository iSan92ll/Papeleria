package io.bootify.papeleria.rest;

import io.bootify.papeleria.model.VentaItemDTO;
import io.bootify.papeleria.service.VentaItemService;
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
@RequestMapping(value = "/api/ventaItems", produces = MediaType.APPLICATION_JSON_VALUE)
public class VentaItemResource {

    private final VentaItemService ventaItemService;

    public VentaItemResource(final VentaItemService ventaItemService) {
        this.ventaItemService = ventaItemService;
    }

    @GetMapping
    public ResponseEntity<List<VentaItemDTO>> getAllVentaItems() {
        return ResponseEntity.ok(ventaItemService.findAll());
    }

    @GetMapping("/{idVentaItem}")
    public ResponseEntity<VentaItemDTO> getVentaItem(
            @PathVariable(name = "idVentaItem") final Long idVentaItem) {
        return ResponseEntity.ok(ventaItemService.get(idVentaItem));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createVentaItem(
            @RequestBody @Valid final VentaItemDTO ventaItemDTO) {
        final Long createdIdVentaItem = ventaItemService.create(ventaItemDTO);
        return new ResponseEntity<>(createdIdVentaItem, HttpStatus.CREATED);
    }

    @PutMapping("/{idVentaItem}")
    public ResponseEntity<Long> updateVentaItem(
            @PathVariable(name = "idVentaItem") final Long idVentaItem,
            @RequestBody @Valid final VentaItemDTO ventaItemDTO) {
        ventaItemService.update(idVentaItem, ventaItemDTO);
        return ResponseEntity.ok(idVentaItem);
    }

    @DeleteMapping("/{idVentaItem}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteVentaItem(
            @PathVariable(name = "idVentaItem") final Long idVentaItem) {
        ventaItemService.delete(idVentaItem);
        return ResponseEntity.noContent().build();
    }

}
