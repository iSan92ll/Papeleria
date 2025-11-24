package io.bootify.papeleria.service;

import io.bootify.papeleria.domain.Usuario;
import io.bootify.papeleria.events.BeforeDeleteUsuario;
import io.bootify.papeleria.model.UsuarioDTO;
import io.bootify.papeleria.repos.UsuarioRepository;
import io.bootify.papeleria.util.NotFoundException;
import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final ApplicationEventPublisher publisher;

    public UsuarioService(final UsuarioRepository usuarioRepository,
            final ApplicationEventPublisher publisher) {
        this.usuarioRepository = usuarioRepository;
        this.publisher = publisher;
    }

    public List<UsuarioDTO> findAll() {
        final List<Usuario> usuarios = usuarioRepository.findAll(Sort.by("idUsuario"));
        return usuarios.stream()
                .map(usuario -> mapToDTO(usuario, new UsuarioDTO()))
                .toList();
    }

    public UsuarioDTO get(final Integer idUsuario) {
        return usuarioRepository.findById(idUsuario)
                .map(usuario -> mapToDTO(usuario, new UsuarioDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final UsuarioDTO usuarioDTO) {
        final Usuario usuario = new Usuario();
        mapToEntity(usuarioDTO, usuario);
        return usuarioRepository.save(usuario).getIdUsuario();
    }

    public void update(final Integer idUsuario, final UsuarioDTO usuarioDTO) {
        final Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(NotFoundException::new);
        mapToEntity(usuarioDTO, usuario);
        usuarioRepository.save(usuario);
    }

    public void delete(final Integer idUsuario) {
        final Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(NotFoundException::new);
        publisher.publishEvent(new BeforeDeleteUsuario(idUsuario));
        usuarioRepository.delete(usuario);
    }

    private UsuarioDTO mapToDTO(final Usuario usuario, final UsuarioDTO usuarioDTO) {
        usuarioDTO.setIdUsuario(usuario.getIdUsuario());
        usuarioDTO.setNombre(usuario.getNombre());
        usuarioDTO.setApellido(usuario.getApellido());
        usuarioDTO.setCorreo(usuario.getCorreo());
        usuarioDTO.setContra(usuario.getContra());
        usuarioDTO.setTelefono(usuario.getTelefono());
        usuarioDTO.setRol(usuario.getRol());
        usuarioDTO.setActivo(usuario.getActivo());
        usuarioDTO.setVerificado(usuario.getVerificado());
        return usuarioDTO;
    }

    private Usuario mapToEntity(final UsuarioDTO usuarioDTO, final Usuario usuario) {
        usuario.setNombre(usuarioDTO.getNombre());
        usuario.setApellido(usuarioDTO.getApellido());
        usuario.setCorreo(usuarioDTO.getCorreo());
        usuario.setContra(usuarioDTO.getContra());
        usuario.setTelefono(usuarioDTO.getTelefono());
        usuario.setRol(usuarioDTO.getRol());
        usuario.setActivo(usuarioDTO.getActivo());
        usuario.setVerificado(usuarioDTO.getVerificado());
        return usuario;
    }

    public boolean correoExists(final String correo) {
        return usuarioRepository.existsByCorreoIgnoreCase(correo);
    }

}
