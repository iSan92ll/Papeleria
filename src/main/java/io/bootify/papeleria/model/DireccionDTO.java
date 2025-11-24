package io.bootify.papeleria.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class DireccionDTO {

    private Integer idDireccion;

    @NotNull
    @Size(max = 300)
    private String direccionTexto;

    @NotNull
    @Size(max = 100)
    private String ciudad;

    @NotNull
    private Double latitud;

    @NotNull
    private Double longitud;

    @NotNull
    private Boolean esPredeterminada;

    @NotNull
    private Boolean activo;

    @NotNull
    private Integer usuario;

}
