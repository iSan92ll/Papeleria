package io.bootify.papeleria.model;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CarritoDTO {

    private Long idCarrito;

    @NotNull
    private Boolean activo;

    @NotNull
    private Integer usuario;

}
