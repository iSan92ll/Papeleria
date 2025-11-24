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
public class VentaItemDTO {

    private Long idVentaItem;

    @NotNull
    @Size(max = 150)
    private String nombreProducto;

    @NotNull
    private Integer cantidad;

    @NotNull
    @Digits(integer = 12, fraction = 2)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(type = "string", example = "77.08")
    private BigDecimal precioUnitario;

    @NotNull
    private Long venta;

    @NotNull
    private Long producto;

}
