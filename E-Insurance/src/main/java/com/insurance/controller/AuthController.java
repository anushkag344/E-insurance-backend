package com.insurance.controller;

import com.insurance.dto.CustomerRegisterRequestDTO;
import com.insurance.dto.LoginRequestDTO;
import com.insurance.dto.LoginResponseDTO;
import com.insurance.dto.RegisterRequestDTO;
import com.insurance.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(
            @Valid @RequestBody RegisterRequestDTO request) {

        return ResponseEntity.ok(
                authService.register(request)
        );
    }
    @PostMapping("/register/customer")
    public ResponseEntity<String> registerCustomer(
            @Valid @RequestBody
            CustomerRegisterRequestDTO request) {

        return ResponseEntity.ok(
                authService.registerCustomer(request)
        );
    }
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO request) {

        return ResponseEntity.ok(
                authService.login(request)
        );
    }
}