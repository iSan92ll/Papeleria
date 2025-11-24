package io.bootify.papeleria.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class TokenVerificacionDTO {

    private Long idToken;

    @NotNull
    @Size(max = 180)
    private String token;

    @NotNull
    private OffsetDateTime fechaExpira;

    @NotNull
    private Boolean usado;

    @NotNull
    private Integer usuario;

}
