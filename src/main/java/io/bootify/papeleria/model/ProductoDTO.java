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
public class ProductoDTO {

    private Long idProducto;

    @NotNull
    @Size(max = 150)
    private String nombre;

    private String descripcion;

    @NotNull
    @Digits(integer = 12, fraction = 2)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(type = "string", example = "90.08")
    private BigDecimal precio;

    @NotNull
    private Integer stock;

    @NotNull
    private Boolean activo;

    @Size(max = 300)
    private String imagenUrl;

}
