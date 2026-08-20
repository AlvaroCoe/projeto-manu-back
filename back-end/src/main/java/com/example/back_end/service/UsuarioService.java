package com.example.back_end.service;

import com.example.back_end.dto.UsuarioCreateDTO;
import com.example.back_end.dto.UsuarioResponseDTO;
import com.example.back_end.entity.UsuarioEntity;
import com.example.back_end.exception.ResourceNotFoundException;
import com.example.back_end.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UsuarioResponseDTO create(UsuarioCreateDTO dto) {
        UsuarioEntity entity = new UsuarioEntity();
        entity.setNome(dto.nome());
        entity.setEmail(dto.email());
        entity.setSenha(passwordEncoder.encode(dto.senha())); // nunca salva senha em texto puro
        entity.setRole(dto.role());

        entity = usuarioRepository.save(entity);
        return new UsuarioResponseDTO(entity);
    }

    public List<UsuarioResponseDTO> findAll() {
        return usuarioRepository.findAll()
                .stream()
                .map(UsuarioResponseDTO::new)
                .toList();
    }

    public UsuarioResponseDTO findById(Long id) {
        return new UsuarioResponseDTO(findEntityById(id));
    }

    // Usado internamente por outros services (ex: TicketService) que precisam da entidade completa
    public UsuarioEntity findEntityById(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado. Id: " + id));
    }
}
