package io.bootify.papeleria.domain;

import io.bootify.papeleria.model.RolUsuario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Usuario {

    @Id
    @Column(nullable = false, updatable = false)
    @SequenceGenerator(
            name = "primary_sequence",
            sequenceName = "primary_sequence",
            allocationSize = 1,
            initialValue = 10000
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "primary_sequence"
    )
    private Integer idUsuario;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(nullable = false, length = 120)
    private String apellido;

    @Column(nullable = false, unique = true, length = 180)
    private String correo;

    @Column(nullable = false)
    private String contra;

    @Column(nullable = false, length = 20)
    private String telefono;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RolUsuario rol;

    @Column(nullable = false)
    private Boolean activo;

    @Column(nullable = false)
    private Boolean verificado;

    @OneToMany(mappedBy = "usuario")
    private Set<Direccion> usuarioDireccions = new HashSet<>();

    @OneToMany(mappedBy = "usuario")
    private Set<TokenVerificacion> usuarioTokenVerificacions = new HashSet<>();

    @OneToMany(mappedBy = "usuario")
    private Set<Carrito> usuarioCarritoes = new HashSet<>();

    @OneToMany(mappedBy = "usuario")
    private Set<Venta> usuarioVentas = new HashSet<>();

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private OffsetDateTime dateCreated;

    @LastModifiedDate
    @Column(nullable = false)
    private OffsetDateTime lastUpdated;

}
