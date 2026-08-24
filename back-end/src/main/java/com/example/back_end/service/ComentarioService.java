package com.example.back_end.service;

import com.example.back_end.dto.ComentarioCreateDTO;
import com.example.back_end.dto.ComentarioResponseDTO;
import com.example.back_end.entity.ComentarioEntity;
import com.example.back_end.entity.TicketEntity;
import com.example.back_end.entity.UsuarioEntity;
import com.example.back_end.repository.ComentarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ComentarioService {

    @Autowired
    private ComentarioRepository comentarioRepository;

    @Autowired
    private TicketService ticketService;

    @Autowired
    private UsuarioService usuarioService;

    public ComentarioResponseDTO create(Long ticketId, ComentarioCreateDTO dto, String emailAutor) {
        TicketEntity ticket = ticketService.findEntityById(ticketId);
        UsuarioEntity autor = usuarioService.findEntityByEmail(emailAutor);

        ComentarioEntity comentario = new ComentarioEntity();
        comentario.setMensagem(dto.mensagem());
        comentario.setTicket(ticket);
        comentario.setAutor(autor);

        comentario = comentarioRepository.save(comentario);
        return new ComentarioResponseDTO(comentario);
    }

    public List<ComentarioResponseDTO> findByTicket(Long ticketId) {
        return comentarioRepository.findByTicketIdOrderByCreatedAtAsc(ticketId)
                .stream()
                .map(ComentarioResponseDTO::new)
                .toList();
    }
}