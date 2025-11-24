package io.bootify.papeleria.model;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CarritoItemDTO {

    private Long idCarritoItem;

    @NotNull
    private Integer cantidad;

    @NotNull
    private Long carrito;

    @NotNull
    private Long producto;

}
