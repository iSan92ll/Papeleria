package io.bootify.papeleria.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class VentaDTO {

    private Long idVenta;

    @NotNull
    @Digits(integer = 14, fraction = 2)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(type = "string", example = "32.08")
    private BigDecimal total;

    @NotNull
    @Size(max = 255)
    private String metodoPago;

    @NotNull
    private EstadoVenta estado;

    @NotNull
    private Integer distanciaMetros;

    @NotNull
    private Integer duracionSegundos;

    @NotNull
    private Integer usuario;

    @NotNull
    private Integer direccion;

}
