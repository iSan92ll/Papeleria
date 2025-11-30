package io.bootify.papeleria.web;

import io.bootify.papeleria.dto.*;
import io.bootify.papeleria.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // Registro: devuelve token de verificación para que el frontend lo envíe por EmailJS
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        String token = authService.register(request);
        return ResponseEntity.ok(new RegisterResponse(token, request.getCorreo(), request.getNombre()));
    }

    // Login: devuelve JWT y user info
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    // Verificación: marca la cuenta verificada (token enviado desde el frontend)
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse> verify(@RequestBody VerifyRequest request) {
        authService.verifyToken(request.getToken());
        return ResponseEntity.ok(new ApiResponse(true, "Cuenta verificada correctamente."));
    }

    // Reenviar verificación: crea token y lo devuelve para que frontend lo envíe
    @PostMapping("/resend-verification")
    public ResponseEntity<RegisterResponse> resendVerification(@RequestBody ResendRequest request) {
        String token = authService.resendVerification(request.getEmail());
        return ResponseEntity.ok(new RegisterResponse(token, request.getEmail(), null));
    }

    // Olvidé contraseña: crea token y lo devuelve para que frontend lo envíe
    @PostMapping("/forgot-password")
    public ResponseEntity<ForgotPasswordResponse> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        String token = authService.forgotPassword(request.getEmail());
        return ResponseEntity.ok(new ForgotPasswordResponse(token, request.getEmail()));
    }

    // Restablecer contraseña: backend aplica el cambio (token validado)
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok(new ApiResponse(true, "Contraseña restablecida correctamente."));
    }
}