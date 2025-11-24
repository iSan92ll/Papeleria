package io.bootify.papeleria.service;

import io.bootify.papeleria.domain.Direccion;
import io.bootify.papeleria.domain.Usuario;
import io.bootify.papeleria.domain.Venta;
import io.bootify.papeleria.events.BeforeDeleteDireccion;
import io.bootify.papeleria.events.BeforeDeleteUsuario;
import io.bootify.papeleria.events.BeforeDeleteVenta;
import io.bootify.papeleria.model.VentaDTO;
import io.bootify.papeleria.repos.DireccionRepository;
import io.bootify.papeleria.repos.UsuarioRepository;
import io.bootify.papeleria.repos.VentaRepository;
import io.bootify.papeleria.util.NotFoundException;
import io.bootify.papeleria.util.ReferencedException;
import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class VentaService {

    private final VentaRepository ventaRepository;
    private final UsuarioRepository usuarioRepository;
    private final DireccionRepository direccionRepository;
    private final ApplicationEventPublisher publisher;

    public VentaService(final VentaRepository ventaRepository,
            final UsuarioRepository usuarioRepository,
            final DireccionRepository direccionRepository,
            final ApplicationEventPublisher publisher) {
        this.ventaRepository = ventaRepository;
        this.usuarioRepository = usuarioRepository;
        this.direccionRepository = direccionRepository;
        this.publisher = publisher;
    }

    public List<VentaDTO> findAll() {
        final List<Venta> ventas = ventaRepository.findAll(Sort.by("idVenta"));
        return ventas.stream()
                .map(venta -> mapToDTO(venta, new VentaDTO()))
                .toList();
    }

    public VentaDTO get(final Long idVenta) {
        return ventaRepository.findById(idVenta)
                .map(venta -> mapToDTO(venta, new VentaDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final VentaDTO ventaDTO) {
        final Venta venta = new Venta();
        mapToEntity(ventaDTO, venta);
        return ventaRepository.save(venta).getIdVenta();
    }

    public void update(final Long idVenta, final VentaDTO ventaDTO) {
        final Venta venta = ventaRepository.findById(idVenta)
                .orElseThrow(NotFoundException::new);
        mapToEntity(ventaDTO, venta);
        ventaRepository.save(venta);
    }

    public void delete(final Long idVenta) {
        final Venta venta = ventaRepository.findById(idVenta)
                .orElseThrow(NotFoundException::new);
        publisher.publishEvent(new BeforeDeleteVenta(idVenta));
        ventaRepository.delete(venta);
    }

    private VentaDTO mapToDTO(final Venta venta, final VentaDTO ventaDTO) {
        ventaDTO.setIdVenta(venta.getIdVenta());
        ventaDTO.setTotal(venta.getTotal());
        ventaDTO.setMetodoPago(venta.getMetodoPago());
        ventaDTO.setEstado(venta.getEstado());
        ventaDTO.setDistanciaMetros(venta.getDistanciaMetros());
        ventaDTO.setDuracionSegundos(venta.getDuracionSegundos());
        ventaDTO.setUsuario(venta.getUsuario() == null ? null : venta.getUsuario().getIdUsuario());
        ventaDTO.setDireccion(venta.getDireccion() == null ? null : venta.getDireccion().getIdDireccion());
        return ventaDTO;
    }

    private Venta mapToEntity(final VentaDTO ventaDTO, final Venta venta) {
        venta.setTotal(ventaDTO.getTotal());
        venta.setMetodoPago(ventaDTO.getMetodoPago());
        venta.setEstado(ventaDTO.getEstado());
        venta.setDistanciaMetros(ventaDTO.getDistanciaMetros());
        venta.setDuracionSegundos(ventaDTO.getDuracionSegundos());
        final Usuario usuario = ventaDTO.getUsuario() == null ? null : usuarioRepository.findById(ventaDTO.getUsuario())
                .orElseThrow(() -> new NotFoundException("usuario not found"));
        venta.setUsuario(usuario);
        final Direccion direccion = ventaDTO.getDireccion() == null ? null : direccionRepository.findById(ventaDTO.getDireccion())
                .orElseThrow(() -> new NotFoundException("direccion not found"));
        venta.setDireccion(direccion);
        return venta;
    }

    @EventListener(BeforeDeleteUsuario.class)
    public void on(final BeforeDeleteUsuario event) {
        final ReferencedException referencedException = new ReferencedException();
        final Venta usuarioVenta = ventaRepository.findFirstByUsuarioIdUsuario(event.getIdUsuario());
        if (usuarioVenta != null) {
            referencedException.setKey("usuario.venta.usuario.referenced");
            referencedException.addParam(usuarioVenta.getIdVenta());
            throw referencedException;
        }
    }

    @EventListener(BeforeDeleteDireccion.class)
    public void on(final BeforeDeleteDireccion event) {
        final ReferencedException referencedException = new ReferencedException();
        final Venta direccionVenta = ventaRepository.findFirstByDireccionIdDireccion(event.getIdDireccion());
        if (direccionVenta != null) {
            referencedException.setKey("direccion.venta.direccion.referenced");
            referencedException.addParam(direccionVenta.getIdVenta());
            throw referencedException;
        }
    }

}
