package io.bootify.papeleria.rest;

import io.bootify.papeleria.model.VentaDTO;
import io.bootify.papeleria.service.VentaService;
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
@RequestMapping(value = "/api/ventas", produces = MediaType.APPLICATION_JSON_VALUE)
public class VentaResource {

    private final VentaService ventaService;

    public VentaResource(final VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @GetMapping
    public ResponseEntity<List<VentaDTO>> getAllVentas() {
        return ResponseEntity.ok(ventaService.findAll());
    }

    @GetMapping("/{idVenta}")
    public ResponseEntity<VentaDTO> getVenta(@PathVariable(name = "idVenta") final Long idVenta) {
        return ResponseEntity.ok(ventaService.get(idVenta));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createVenta(@RequestBody @Valid final VentaDTO ventaDTO) {
        final Long createdIdVenta = ventaService.create(ventaDTO);
        return new ResponseEntity<>(createdIdVenta, HttpStatus.CREATED);
    }

    @PutMapping("/{idVenta}")
    public ResponseEntity<Long> updateVenta(@PathVariable(name = "idVenta") final Long idVenta,
            @RequestBody @Valid final VentaDTO ventaDTO) {
        ventaService.update(idVenta, ventaDTO);
        return ResponseEntity.ok(idVenta);
    }

    @DeleteMapping("/{idVenta}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteVenta(@PathVariable(name = "idVenta") final Long idVenta) {
        ventaService.delete(idVenta);
        return ResponseEntity.noContent().build();
    }

}
