package com.example.back_end.service;

import com.example.back_end.dto.TicketCreateDTO;
import com.example.back_end.dto.TicketEscalateDTO;
import com.example.back_end.dto.TicketResponseDTO;
import com.example.back_end.dto.TicketStatusUpdateDTO;
import com.example.back_end.entity.EquipamentoEntity;
import com.example.back_end.entity.TicketEntity;
import com.example.back_end.entity.UsuarioEntity;
import com.example.back_end.enums.SupportLevel;
import com.example.back_end.enums.TicketStatus;
import com.example.back_end.exception.ResourceNotFoundException;
import com.example.back_end.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private EquipamentoService equipamentoService;

    public TicketResponseDTO create(TicketCreateDTO dto) {
        UsuarioEntity client = usuarioService.findEntityById(dto.clientId());
        EquipamentoEntity equipamento = equipamentoService.findEntityById(dto.equipamentoId());

        TicketEntity entity = new TicketEntity();
        entity.setTitulo(dto.titulo());
        entity.setDescricao(dto.descricao());
        entity.setPrioridade(dto.prioridade());
        entity.setImageUrl(dto.imageUrl());
        entity.setClient(client);
        entity.setEquipamento(equipamento);
        // status = ABERTO e currentLevel = N1 já vêm definidos no construtor de TicketEntity

        entity = ticketRepository.save(entity);
        return new TicketResponseDTO(entity);
    }

    public List<TicketResponseDTO> findAll() {
        return ticketRepository.findAll()
                .stream()
                .map(TicketResponseDTO::new)
                .toList();
    }

    public TicketResponseDTO findById(Long id) {
        return new TicketResponseDTO(findEntityById(id));
    }

    public TicketEntity findEntityById(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chamado não encontrado. Id: " + id));
    }

    public TicketResponseDTO updateStatus(Long id, TicketStatusUpdateDTO dto) {
        TicketEntity entity = findEntityById(id);
        entity.setStatus(dto.status());

        entity = ticketRepository.save(entity);
        return new TicketResponseDTO(entity);
    }

    // Regra de negócio principal: N1 -> N2 -> N3. Não é possível escalar além de N3.
    public TicketResponseDTO escalar(Long id, TicketEscalateDTO dto) {
        TicketEntity entity = findEntityById(id);

        SupportLevel proximoNivel = proximoNivel(entity.getCurrentLevel());
        entity.setCurrentLevel(proximoNivel);
        entity.setStatus(TicketStatus.ANDAMENTO);

        if (dto.technicianId() != null) {
            UsuarioEntity tecnico = usuarioService.findEntityById(dto.technicianId());
            entity.setTechnician(tecnico);
        }

        entity = ticketRepository.save(entity);
        return new TicketResponseDTO(entity);
    }

    private SupportLevel proximoNivel(SupportLevel atual) {
        return switch (atual) {
            case N1 -> SupportLevel.N2;
            case N2 -> SupportLevel.N3;
            case N3 -> throw new IllegalStateException(
                    "O chamado já está no nível máximo de suporte (N3) e não pode ser escalado.");
        };
    }
}
