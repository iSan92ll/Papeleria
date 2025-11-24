package io.bootify.papeleria.service;

import io.bootify.papeleria.domain.Direccion;
import io.bootify.papeleria.domain.Usuario;
import io.bootify.papeleria.events.BeforeDeleteDireccion;
import io.bootify.papeleria.events.BeforeDeleteUsuario;
import io.bootify.papeleria.model.DireccionDTO;
import io.bootify.papeleria.repos.DireccionRepository;
import io.bootify.papeleria.repos.UsuarioRepository;
import io.bootify.papeleria.util.NotFoundException;
import io.bootify.papeleria.util.ReferencedException;
import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class DireccionService {

    private final DireccionRepository direccionRepository;
    private final UsuarioRepository usuarioRepository;
    private final ApplicationEventPublisher publisher;

    public DireccionService(final DireccionRepository direccionRepository,
            final UsuarioRepository usuarioRepository, final ApplicationEventPublisher publisher) {
        this.direccionRepository = direccionRepository;
        this.usuarioRepository = usuarioRepository;
        this.publisher = publisher;
    }

    public List<DireccionDTO> findAll() {
        final List<Direccion> direccions = direccionRepository.findAll(Sort.by("idDireccion"));
        return direccions.stream()
                .map(direccion -> mapToDTO(direccion, new DireccionDTO()))
                .toList();
    }

    public DireccionDTO get(final Integer idDireccion) {
        return direccionRepository.findById(idDireccion)
                .map(direccion -> mapToDTO(direccion, new DireccionDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final DireccionDTO direccionDTO) {
        final Direccion direccion = new Direccion();
        mapToEntity(direccionDTO, direccion);
        return direccionRepository.save(direccion).getIdDireccion();
    }

    public void update(final Integer idDireccion, final DireccionDTO direccionDTO) {
        final Direccion direccion = direccionRepository.findById(idDireccion)
                .orElseThrow(NotFoundException::new);
        mapToEntity(direccionDTO, direccion);
        direccionRepository.save(direccion);
    }

    public void delete(final Integer idDireccion) {
        final Direccion direccion = direccionRepository.findById(idDireccion)
                .orElseThrow(NotFoundException::new);
        publisher.publishEvent(new BeforeDeleteDireccion(idDireccion));
        direccionRepository.delete(direccion);
    }

    private DireccionDTO mapToDTO(final Direccion direccion, final DireccionDTO direccionDTO) {
        direccionDTO.setIdDireccion(direccion.getIdDireccion());
        direccionDTO.setDireccionTexto(direccion.getDireccionTexto());
        direccionDTO.setCiudad(direccion.getCiudad());
        direccionDTO.setLatitud(direccion.getLatitud());
        direccionDTO.setLongitud(direccion.getLongitud());
        direccionDTO.setEsPredeterminada(direccion.getEsPredeterminada());
        direccionDTO.setActivo(direccion.getActivo());
        direccionDTO.setUsuario(direccion.getUsuario() == null ? null : direccion.getUsuario().getIdUsuario());
        return direccionDTO;
    }

    private Direccion mapToEntity(final DireccionDTO direccionDTO, final Direccion direccion) {
        direccion.setDireccionTexto(direccionDTO.getDireccionTexto());
        direccion.setCiudad(direccionDTO.getCiudad());
        direccion.setLatitud(direccionDTO.getLatitud());
        direccion.setLongitud(direccionDTO.getLongitud());
        direccion.setEsPredeterminada(direccionDTO.getEsPredeterminada());
        direccion.setActivo(direccionDTO.getActivo());
        final Usuario usuario = direccionDTO.getUsuario() == null ? null : usuarioRepository.findById(direccionDTO.getUsuario())
                .orElseThrow(() -> new NotFoundException("usuario not found"));
        direccion.setUsuario(usuario);
        return direccion;
    }

    @EventListener(BeforeDeleteUsuario.class)
    public void on(final BeforeDeleteUsuario event) {
        final ReferencedException referencedException = new ReferencedException();
        final Direccion usuarioDireccion = direccionRepository.findFirstByUsuarioIdUsuario(event.getIdUsuario());
        if (usuarioDireccion != null) {
            referencedException.setKey("usuario.direccion.usuario.referenced");
            referencedException.addParam(usuarioDireccion.getIdDireccion());
            throw referencedException;
        }
    }

}
