package io.bootify.papeleria.rest;

import io.bootify.papeleria.model.TokenVerificacionDTO;
import io.bootify.papeleria.service.TokenVerificacionService;
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
@RequestMapping(value = "/api/tokenVerificacions", produces = MediaType.APPLICATION_JSON_VALUE)
public class TokenVerificacionResource {

    private final TokenVerificacionService tokenVerificacionService;

    public TokenVerificacionResource(final TokenVerificacionService tokenVerificacionService) {
        this.tokenVerificacionService = tokenVerificacionService;
    }

    @GetMapping
    public ResponseEntity<List<TokenVerificacionDTO>> getAllTokenVerificacions() {
        return ResponseEntity.ok(tokenVerificacionService.findAll());
    }

    @GetMapping("/{idToken}")
    public ResponseEntity<TokenVerificacionDTO> getTokenVerificacion(
            @PathVariable(name = "idToken") final Long idToken) {
        return ResponseEntity.ok(tokenVerificacionService.get(idToken));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createTokenVerificacion(
            @RequestBody @Valid final TokenVerificacionDTO tokenVerificacionDTO) {
        final Long createdIdToken = tokenVerificacionService.create(tokenVerificacionDTO);
        return new ResponseEntity<>(createdIdToken, HttpStatus.CREATED);
    }

    @PutMapping("/{idToken}")
    public ResponseEntity<Long> updateTokenVerificacion(
            @PathVariable(name = "idToken") final Long idToken,
            @RequestBody @Valid final TokenVerificacionDTO tokenVerificacionDTO) {
        tokenVerificacionService.update(idToken, tokenVerificacionDTO);
        return ResponseEntity.ok(idToken);
    }

    @DeleteMapping("/{idToken}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteTokenVerificacion(
            @PathVariable(name = "idToken") final Long idToken) {
        tokenVerificacionService.delete(idToken);
        return ResponseEntity.noContent().build();
    }

}
