package com.example.back_end.controller;

import com.example.back_end.dto.TicketCreateDTO;
import com.example.back_end.dto.TicketEscalateDTO;
import com.example.back_end.dto.TicketResponseDTO;
import com.example.back_end.dto.TicketStatusUpdateDTO;
import com.example.back_end.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @PostMapping
    public ResponseEntity<TicketResponseDTO> create(@Valid @RequestBody TicketCreateDTO dto) {
        TicketResponseDTO response = ticketService.create(dto);
        URI uri = URI.create("/api/tickets/" + response.id());
        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping
    public ResponseEntity<List<TicketResponseDTO>> findAll() {
        return ResponseEntity.ok(ticketService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.findById(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TicketResponseDTO> updateStatus(@PathVariable Long id,
                                                          @Valid @RequestBody TicketStatusUpdateDTO dto) {
        return ResponseEntity.ok(ticketService.updateStatus(id, dto));
    }

    @PatchMapping("/{id}/escalar")
    public ResponseEntity<TicketResponseDTO> escalar(@PathVariable Long id,
                                                     @Valid @RequestBody TicketEscalateDTO dto) {
        return ResponseEntity.ok(ticketService.escalar(id, dto));
    }
}
