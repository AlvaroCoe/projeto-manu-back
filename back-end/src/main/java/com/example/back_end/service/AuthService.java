package com.example.back_end.service;

import com.example.back_end.dto.LoginRequestDTO;
import com.example.back_end.dto.LoginResponseDTO;
import com.example.back_end.entity.UsuarioEntity;
import com.example.back_end.exception.ResourceNotFoundException;
import com.example.back_end.repository.UsuarioRepository;
import com.example.back_end.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public LoginResponseDTO login(LoginRequestDTO dto) {
        UsuarioEntity usuario = usuarioRepository.findByEmail(dto.email())
                .orElseThrow(() -> new ResourceNotFoundException("E-mail ou senha inválidos"));

        if (!passwordEncoder.matches(dto.senha(), usuario.getSenha())) {
            throw new ResourceNotFoundException("E-mail ou senha inválidos");
        }

        String token = jwtUtil.generateToken(usuario.getEmail(), usuario.getRole().name());

        return new LoginResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getRole(),
                token
        );
    }
}