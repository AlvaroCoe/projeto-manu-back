package com.example.back_end.controller;

import com.example.back_end.dto.AlterarSenhaDTO;
import com.example.back_end.dto.MensagemDTO;
import com.example.back_end.dto.UsuarioCreateDTO;
import com.example.back_end.dto.UsuarioResponseDTO;
import com.example.back_end.dto.UsuarioStatusUpdateDTO;
import com.example.back_end.dto.UsuarioUpdateDTO;
import com.example.back_end.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // Qualquer usuário autenticado (Solicitante, N1, N2, N3 ou Admin) pode trocar a própria senha.
    // Sem @PreAuthorize de propósito: a única regra é estar logado (já garantido pelo SecurityConfig).
    @PatchMapping("/me/senha")
    public ResponseEntity<MensagemDTO> alterarMinhaSenha(@Valid @RequestBody AlterarSenhaDTO dto,
                                                         Authentication authentication) {
        return ResponseEntity.ok(usuarioService.alterarSenha(authentication.getName(), dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> create(@Valid @RequestBody UsuarioCreateDTO dto) {
        UsuarioResponseDTO response = usuarioService.create(dto);
        URI uri = URI.create("/api/usuarios/" + response.id());
        return ResponseEntity.created(uri).body(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> findAll() {
        return ResponseEntity.ok(usuarioService.findAll());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.findById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> update(@PathVariable Long id, @Valid @RequestBody UsuarioUpdateDTO dto) {
        return ResponseEntity.ok(usuarioService.update(id, dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<UsuarioResponseDTO> atualizarStatus(@PathVariable Long id, @Valid @RequestBody UsuarioStatusUpdateDTO dto) {
        return ResponseEntity.ok(usuarioService.atualizarStatus(id, dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        usuarioService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}