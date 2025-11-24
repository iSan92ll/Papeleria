package io.bootify.papeleria.service;

import io.bootify.papeleria.domain.Carrito;
import io.bootify.papeleria.domain.Usuario;
import io.bootify.papeleria.events.BeforeDeleteCarrito;
import io.bootify.papeleria.events.BeforeDeleteUsuario;
import io.bootify.papeleria.model.CarritoDTO;
import io.bootify.papeleria.repos.CarritoRepository;
import io.bootify.papeleria.repos.UsuarioRepository;
import io.bootify.papeleria.util.NotFoundException;
import io.bootify.papeleria.util.ReferencedException;
import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class CarritoService {

    private final CarritoRepository carritoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ApplicationEventPublisher publisher;

    public CarritoService(final CarritoRepository carritoRepository,
            final UsuarioRepository usuarioRepository, final ApplicationEventPublisher publisher) {
        this.carritoRepository = carritoRepository;
        this.usuarioRepository = usuarioRepository;
        this.publisher = publisher;
    }

    public List<CarritoDTO> findAll() {
        final List<Carrito> carritoes = carritoRepository.findAll(Sort.by("idCarrito"));
        return carritoes.stream()
                .map(carrito -> mapToDTO(carrito, new CarritoDTO()))
                .toList();
    }

    public CarritoDTO get(final Long idCarrito) {
        return carritoRepository.findById(idCarrito)
                .map(carrito -> mapToDTO(carrito, new CarritoDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final CarritoDTO carritoDTO) {
        final Carrito carrito = new Carrito();
        mapToEntity(carritoDTO, carrito);
        return carritoRepository.save(carrito).getIdCarrito();
    }

    public void update(final Long idCarrito, final CarritoDTO carritoDTO) {
        final Carrito carrito = carritoRepository.findById(idCarrito)
                .orElseThrow(NotFoundException::new);
        mapToEntity(carritoDTO, carrito);
        carritoRepository.save(carrito);
    }

    public void delete(final Long idCarrito) {
        final Carrito carrito = carritoRepository.findById(idCarrito)
                .orElseThrow(NotFoundException::new);
        publisher.publishEvent(new BeforeDeleteCarrito(idCarrito));
        carritoRepository.delete(carrito);
    }

    private CarritoDTO mapToDTO(final Carrito carrito, final CarritoDTO carritoDTO) {
        carritoDTO.setIdCarrito(carrito.getIdCarrito());
        carritoDTO.setActivo(carrito.getActivo());
        carritoDTO.setUsuario(carrito.getUsuario() == null ? null : carrito.getUsuario().getIdUsuario());
        return carritoDTO;
    }

    private Carrito mapToEntity(final CarritoDTO carritoDTO, final Carrito carrito) {
        carrito.setActivo(carritoDTO.getActivo());
        final Usuario usuario = carritoDTO.getUsuario() == null ? null : usuarioRepository.findById(carritoDTO.getUsuario())
                .orElseThrow(() -> new NotFoundException("usuario not found"));
        carrito.setUsuario(usuario);
        return carrito;
    }

    @EventListener(BeforeDeleteUsuario.class)
    public void on(final BeforeDeleteUsuario event) {
        final ReferencedException referencedException = new ReferencedException();
        final Carrito usuarioCarrito = carritoRepository.findFirstByUsuarioIdUsuario(event.getIdUsuario());
        if (usuarioCarrito != null) {
            referencedException.setKey("usuario.carrito.usuario.referenced");
            referencedException.addParam(usuarioCarrito.getIdCarrito());
            throw referencedException;
        }
    }

}
