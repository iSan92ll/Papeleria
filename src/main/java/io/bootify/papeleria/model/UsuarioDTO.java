package io.bootify.papeleria.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UsuarioDTO {

    private Integer idUsuario;

    @NotNull
    @Size(max = 120)
    private String nombre;

    @NotNull
    @Size(max = 120)
    private String apellido;

    @NotNull
    @Size(max = 180)
    @UsuarioCorreoUnique
    private String correo;

    @NotNull
    @Size(max = 255)
    private String contra;

    @NotNull
    @Size(max = 20)
    private String telefono;

    @NotNull
    private RolUsuario rol;

    @NotNull
    private Boolean activo;

    @NotNull
    private Boolean verificado;

}
