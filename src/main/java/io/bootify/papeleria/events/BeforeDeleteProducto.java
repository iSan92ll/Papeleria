package io.bootify.papeleria.events;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class BeforeDeleteProducto {

    private Long idProducto;

}
