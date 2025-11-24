package io.bootify.papeleria.service;

import io.bootify.papeleria.domain.TokenVerificacion;
import io.bootify.papeleria.domain.Usuario;
import io.bootify.papeleria.events.BeforeDeleteUsuario;
import io.bootify.papeleria.model.TokenVerificacionDTO;
import io.bootify.papeleria.repos.TokenVerificacionRepository;
import io.bootify.papeleria.repos.UsuarioRepository;
import io.bootify.papeleria.util.NotFoundException;
import io.bootify.papeleria.util.ReferencedException;
import java.util.List;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class TokenVerificacionService {

    private final TokenVerificacionRepository tokenVerificacionRepository;
    private final UsuarioRepository usuarioRepository;

    public TokenVerificacionService(final TokenVerificacionRepository tokenVerificacionRepository,
            final UsuarioRepository usuarioRepository) {
        this.tokenVerificacionRepository = tokenVerificacionRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<TokenVerificacionDTO> findAll() {
        final List<TokenVerificacion> tokenVerificacions = tokenVerificacionRepository.findAll(Sort.by("idToken"));
        return tokenVerificacions.stream()
                .map(tokenVerificacion -> mapToDTO(tokenVerificacion, new TokenVerificacionDTO()))
                .toList();
    }

    public TokenVerificacionDTO get(final Long idToken) {
        return tokenVerificacionRepository.findById(idToken)
                .map(tokenVerificacion -> mapToDTO(tokenVerificacion, new TokenVerificacionDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final TokenVerificacionDTO tokenVerificacionDTO) {
        final TokenVerificacion tokenVerificacion = new TokenVerificacion();
        mapToEntity(tokenVerificacionDTO, tokenVerificacion);
        return tokenVerificacionRepository.save(tokenVerificacion).getIdToken();
    }

    public void update(final Long idToken, final TokenVerificacionDTO tokenVerificacionDTO) {
        final TokenVerificacion tokenVerificacion = tokenVerificacionRepository.findById(idToken)
                .orElseThrow(NotFoundException::new);
        mapToEntity(tokenVerificacionDTO, tokenVerificacion);
        tokenVerificacionRepository.save(tokenVerificacion);
    }

    public void delete(final Long idToken) {
        final TokenVerificacion tokenVerificacion = tokenVerificacionRepository.findById(idToken)
                .orElseThrow(NotFoundException::new);
        tokenVerificacionRepository.delete(tokenVerificacion);
    }

    private TokenVerificacionDTO mapToDTO(final TokenVerificacion tokenVerificacion,
            final TokenVerificacionDTO tokenVerificacionDTO) {
        tokenVerificacionDTO.setIdToken(tokenVerificacion.getIdToken());
        tokenVerificacionDTO.setToken(tokenVerificacion.getToken());
        tokenVerificacionDTO.setFechaExpira(tokenVerificacion.getFechaExpira());
        tokenVerificacionDTO.setUsado(tokenVerificacion.getUsado());
        tokenVerificacionDTO.setUsuario(tokenVerificacion.getUsuario() == null ? null : tokenVerificacion.getUsuario().getIdUsuario());
        return tokenVerificacionDTO;
    }

    private TokenVerificacion mapToEntity(final TokenVerificacionDTO tokenVerificacionDTO,
            final TokenVerificacion tokenVerificacion) {
        tokenVerificacion.setToken(tokenVerificacionDTO.getToken());
        tokenVerificacion.setFechaExpira(tokenVerificacionDTO.getFechaExpira());
        tokenVerificacion.setUsado(tokenVerificacionDTO.getUsado());
        final Usuario usuario = tokenVerificacionDTO.getUsuario() == null ? null : usuarioRepository.findById(tokenVerificacionDTO.getUsuario())
                .orElseThrow(() -> new NotFoundException("usuario not found"));
        tokenVerificacion.setUsuario(usuario);
        return tokenVerificacion;
    }

    @EventListener(BeforeDeleteUsuario.class)
    public void on(final BeforeDeleteUsuario event) {
        final ReferencedException referencedException = new ReferencedException();
        final TokenVerificacion usuarioTokenVerificacion = tokenVerificacionRepository.findFirstByUsuarioIdUsuario(event.getIdUsuario());
        if (usuarioTokenVerificacion != null) {
            referencedException.setKey("usuario.tokenVerificacion.usuario.referenced");
            referencedException.addParam(usuarioTokenVerificacion.getIdToken());
            throw referencedException;
        }
    }

}
