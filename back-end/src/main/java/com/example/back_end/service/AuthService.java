package com.example.back_end.service;

import com.example.back_end.dto.LoginRequestDTO;
import com.example.back_end.dto.LoginResponseDTO;
import com.example.back_end.entity.UsuarioEntity;
import com.example.back_end.exception.ResourceNotFoundException;
import com.example.back_end.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public LoginResponseDTO login(LoginRequestDTO dto) {
        // 1. Busca o usuário pelo e-mail
        UsuarioEntity usuario = usuarioRepository.findByEmail(dto.email())
                .orElseThrow(() -> new ResourceNotFoundException("E-mail ou senha inválidos"));

        // 2. Compara a senha informada com o hash salvo no banco
        if (!passwordEncoder.matches(dto.senha(), usuario.getSenha())) {
            throw new ResourceNotFoundException("E-mail ou senha inválidos");
        }

        // 3. Retorna os dados do usuário logado
        return new LoginResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getRole(),
                "fake-jwt-token-12345" // Token simulado
        );
    }
}