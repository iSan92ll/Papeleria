package io.bootify.papeleria.service;

import io.bootify.papeleria.domain.TokenVerificacion;
import io.bootify.papeleria.domain.Usuario;
import io.bootify.papeleria.security.JwtUtil;
import io.bootify.papeleria.dto.*;
import io.bootify.papeleria.model.RolUsuario;
import io.bootify.papeleria.repos.TokenVerificacionRepository;
import io.bootify.papeleria.repos.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final TokenVerificacionRepository tokenRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UsuarioRepository usuarioRepository,
                       TokenVerificacionRepository tokenRepository,
                       BCryptPasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.usuarioRepository = usuarioRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    // Devuelve token de verificación (frontend lo enviará por EmailJS)
    @Transactional
    public String register(RegisterRequest req) {
        if (usuarioRepository.existsByCorreoIgnoreCase(req.getCorreo())) {
            throw new IllegalArgumentException("Correo ya registrado");
        }

        Usuario u = new Usuario();
        u.setNombre(req.getNombre());
        u.setApellido(req.getApellido());
        u.setCorreo(req.getCorreo());
        u.setContra(passwordEncoder.encode(req.getContra()));
        u.setTelefono(req.getTelefono());
        u.setRol(RolUsuario.CLIENTE);
        u.setActivo(true);
        u.setVerificado(false);

        usuarioRepository.save(u);

        // crear token de verificación
        TokenVerificacion t = new TokenVerificacion();
        t.setToken(UUID.randomUUID().toString());
        t.setFechaExpira(OffsetDateTime.now().plusHours(24));
        t.setUsado(false);
        t.setUsuario(u);
        tokenRepository.save(t);

        return t.getToken();
    }

    public LoginResponse login(LoginRequest req) {
        Optional<Usuario> opt = usuarioRepository.findByCorreo(req.getCorreo());
        Usuario u = opt.orElseThrow(() -> new IllegalArgumentException("Credenciales inválidas"));

        if (!passwordEncoder.matches(req.getContra(), u.getContra())) {
            throw new IllegalArgumentException("Credenciales inválidas");
        }

        if (!u.getActivo()) {
            throw new IllegalStateException("Cuenta inactiva");
        }

        String token = jwtUtil.generateToken(u.getCorreo(), u.getRol().name());

        UserDto dto = new UserDto();
        dto.setIdUsuario(u.getIdUsuario());
        dto.setNombre(u.getNombre());
        dto.setApellido(u.getApellido());
        dto.setCorreo(u.getCorreo());
        dto.setTelefono(u.getTelefono());
        dto.setRol(u.getRol().name());
        dto.setActivo(u.getActivo());
        dto.setVerificado(u.getVerificado());

        return new LoginResponse(token, dto);
    }

    @Transactional
    public void verifyToken(String token) {
        TokenVerificacion t = tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Token inválido"));
        if (t.getUsado()) throw new IllegalArgumentException("Token ya usado");
        if (t.getFechaExpira().isBefore(OffsetDateTime.now())) throw new IllegalArgumentException("Token expirado");

        Usuario u = t.getUsuario();
        u.setVerificado(true);
        usuarioRepository.save(u);

        t.setUsado(true);
        tokenRepository.save(t);
    }

    // Crea y devuelve nuevo token de verificación (frontend se encarga de enviarlo)
    @Transactional
    public String resendVerification(String email) {
        Usuario u = usuarioRepository.findByCorreo(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        TokenVerificacion t = new TokenVerificacion();
        t.setToken(UUID.randomUUID().toString());
        t.setFechaExpira(OffsetDateTime.now().plusHours(24));
        t.setUsado(false);
        t.setUsuario(u);
        tokenRepository.save(t);

        return t.getToken();
    }

    // Crea y devuelve token de recuperación (frontend envía el email)
    @Transactional
    public String forgotPassword(String email) {
        Usuario u = usuarioRepository.findByCorreo(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        TokenVerificacion t = new TokenVerificacion();
        t.setToken(UUID.randomUUID().toString());
        t.setFechaExpira(OffsetDateTime.now().plusHours(2));
        t.setUsado(false);
        t.setUsuario(u);
        tokenRepository.save(t);

        return t.getToken();
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        TokenVerificacion t = tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Token inválido"));

        if (t.getUsado()) throw new IllegalArgumentException("Token ya usado");
        if (t.getFechaExpira().isBefore(OffsetDateTime.now())) throw new IllegalArgumentException("Token expirado");

        Usuario u = t.getUsuario();
        u.setContra(passwordEncoder.encode(newPassword));
        usuarioRepository.save(u);

        t.setUsado(true);
        tokenRepository.save(t);
    }
}