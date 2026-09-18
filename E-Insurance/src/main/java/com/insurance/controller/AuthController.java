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
@CrossOrigin(origins = "*")
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping({"/customer/register", "/customer/registar"})
    public ResponseEntity<String> registerCustomer(
            @Valid @RequestBody CustomerRegisterRequestDTO request) {
        return ResponseEntity.ok(authService.registerCustomer(request));
    }

    @PostMapping({"/employee/register", "/employee/registar"})
    public ResponseEntity<String> registerEmployee(
            @Valid @RequestBody RegisterRequestDTO request) {
        return ResponseEntity.ok(authService.registerEmployee(request));
    }

    @PostMapping({"/agent/register", "/agent/registar"})
    public ResponseEntity<String> registerAgent(
            @Valid @RequestBody RegisterRequestDTO request) {
        return ResponseEntity.ok(authService.registerAgent(request));
    }

    @PostMapping({"/admin/register", "/admin/registar"})
    public ResponseEntity<String> registerAdmin(
            @Valid @RequestBody RegisterRequestDTO request) {
        return ResponseEntity.ok(authService.registerAdmin(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
