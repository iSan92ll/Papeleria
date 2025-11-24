package io.bootify.papeleria.rest;

import io.bootify.papeleria.model.DireccionDTO;
import io.bootify.papeleria.service.DireccionService;
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
@RequestMapping(value = "/api/direccions", produces = MediaType.APPLICATION_JSON_VALUE)
public class DireccionResource {

    private final DireccionService direccionService;

    public DireccionResource(final DireccionService direccionService) {
        this.direccionService = direccionService;
    }

    @GetMapping
    public ResponseEntity<List<DireccionDTO>> getAllDireccions() {
        return ResponseEntity.ok(direccionService.findAll());
    }

    @GetMapping("/{idDireccion}")
    public ResponseEntity<DireccionDTO> getDireccion(
            @PathVariable(name = "idDireccion") final Integer idDireccion) {
        return ResponseEntity.ok(direccionService.get(idDireccion));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Integer> createDireccion(
            @RequestBody @Valid final DireccionDTO direccionDTO) {
        final Integer createdIdDireccion = direccionService.create(direccionDTO);
        return new ResponseEntity<>(createdIdDireccion, HttpStatus.CREATED);
    }

    @PutMapping("/{idDireccion}")
    public ResponseEntity<Integer> updateDireccion(
            @PathVariable(name = "idDireccion") final Integer idDireccion,
            @RequestBody @Valid final DireccionDTO direccionDTO) {
        direccionService.update(idDireccion, direccionDTO);
        return ResponseEntity.ok(idDireccion);
    }

    @DeleteMapping("/{idDireccion}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteDireccion(
            @PathVariable(name = "idDireccion") final Integer idDireccion) {
        direccionService.delete(idDireccion);
        return ResponseEntity.noContent().build();
    }

}
