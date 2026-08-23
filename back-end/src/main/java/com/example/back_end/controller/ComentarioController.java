package com.example.back_end.controller;

import com.example.back_end.dto.ComentarioCreateDTO;
import com.example.back_end.dto.ComentarioResponseDTO;
import com.example.back_end.service.ComentarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets/{ticketId}/comments")
public class ComentarioController {

    @Autowired
    private ComentarioService comentarioService;

    @PostMapping
    public ResponseEntity<ComentarioResponseDTO> create(@PathVariable Long ticketId,
                                                        @Valid @RequestBody ComentarioCreateDTO dto,
                                                        Authentication authentication) {
        ComentarioResponseDTO response = comentarioService.create(ticketId, dto, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ComentarioResponseDTO>> findByTicket(@PathVariable Long ticketId) {
        return ResponseEntity.ok(comentarioService.findByTicket(ticketId));
    }
}