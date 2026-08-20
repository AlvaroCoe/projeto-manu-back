package com.example.back_end.controller;

import com.example.back_end.dto.EquipamentoCreateDTO;
import com.example.back_end.dto.EquipamentoResponseDTO;
import com.example.back_end.service.EquipamentoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/equipamentos")
public class EquipamentoController {

    @Autowired
    private EquipamentoService equipamentoService;

    @PostMapping
    public ResponseEntity<EquipamentoResponseDTO> create(@Valid @RequestBody EquipamentoCreateDTO dto) {
        EquipamentoResponseDTO response = equipamentoService.create(dto);
        URI uri = URI.create("/api/equipamentos/" + response.id());
        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping
    public ResponseEntity<List<EquipamentoResponseDTO>> findAll() {
        return ResponseEntity.ok(equipamentoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EquipamentoResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(equipamentoService.findById(id));
    }
}
