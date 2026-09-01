package com.example.back_end.service;

import com.example.back_end.dto.*;
import com.example.back_end.entity.ComentarioEntity;
import com.example.back_end.entity.EquipamentoEntity;
import com.example.back_end.entity.TicketEntity;
import com.example.back_end.entity.UsuarioEntity;
import com.example.back_end.enums.SupportLevel;
import com.example.back_end.enums.TicketStatus;
import com.example.back_end.enums.UserRole;
import com.example.back_end.exception.ResourceNotFoundException;
import com.example.back_end.repository.ComentarioRepository;
import com.example.back_end.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private EquipamentoService equipamentoService;

    @Autowired
    private ComentarioRepository comentarioRepository;

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

        entity = ticketRepository.save(entity);
        return new TicketResponseDTO(entity);
    }

    // clientId e nivel são filtros opcionais:
    // - clientId: usado pelo SOLICITANTE para ver só os próprios chamados ("Meus Chamados")
    // - nivel: usado pelo TÉCNICO para ver a fila de chamados do seu nível de atendimento
    public List<TicketResponseDTO> findAll(Long clientId, SupportLevel nivel) {
        List<TicketEntity> tickets;

        if (clientId != null) {
            tickets = ticketRepository.findByClientId(clientId);
        } else if (nivel != null) {
            tickets = ticketRepository.findByCurrentLevel(nivel);
        } else {
            tickets = ticketRepository.findAll();
        }

        return tickets.stream()
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

    public TicketResponseDTO updateStatus(Long id, TicketStatusUpdateDTO dto, String emailAutor) {
        TicketEntity entity = findEntityById(id);
        validarTecnicoPodeAgir(entity, emailAutor);
        entity.setStatus(dto.status());

        if (dto.status() == TicketStatus.FINALIZADO || dto.status() == TicketStatus.CANCELADO) {
            entity.setResolvedAt(LocalDateTime.now());
        }

        entity = ticketRepository.save(entity);
        return new TicketResponseDTO(entity);
    }

    // Regra de negócio principal: N1 -> N2 -> N3. Não é possível escalar além de N3.
    public TicketResponseDTO escalar(Long id, TicketEscalateDTO dto, String emailAutor) {
        TicketEntity entity = findEntityById(id);
        validarTecnicoPodeAgir(entity, emailAutor);

        SupportLevel nivelAnterior = entity.getCurrentLevel();
        SupportLevel proximoNivel = proximoNivel(nivelAnterior);
        entity.setCurrentLevel(proximoNivel);
        entity.setStatus(TicketStatus.ANDAMENTO);

        // Volta a "sem responsável" no novo nível — alguém DESSE nível precisa assumir de novo.
        entity.setTechnician(null);
        if (dto.technicianId() != null) {
            UsuarioEntity tecnico = usuarioService.findEntityById(dto.technicianId());
            entity.setTechnician(tecnico);
        }

        entity = ticketRepository.save(entity);

        UsuarioEntity autor = usuarioService.findEntityByEmail(emailAutor);
        ComentarioEntity comentario = new ComentarioEntity();
        comentario.setMensagem("Chamado escalado de " + nivelAnterior + " para " + proximoNivel + ". Motivo: " + dto.motivo());
        comentario.setTicket(entity);
        comentario.setAutor(autor);
        comentarioRepository.save(comentario);

        return new TicketResponseDTO(entity);
    }

    // Assumir um chamado exige que o nível do técnico bata EXATAMENTE com o nível do chamado.
    public TicketResponseDTO pegar(Long id, String emailTecnico) {
        TicketEntity entity = findEntityById(id);
        UsuarioEntity tecnico = usuarioService.findEntityByEmail(emailTecnico);

        SupportLevel nivelTecnico = nivelDoRole(tecnico.getRole());
        if (nivelTecnico != entity.getCurrentLevel()) {
            throw new IllegalStateException(
                    "Este chamado está no nível " + entity.getCurrentLevel()
                            + " e só pode ser assumido por um técnico desse mesmo nível."
            );
        }

        entity.setTechnician(tecnico);
        if (entity.getStatus() == TicketStatus.ABERTO) {
            entity.setStatus(TicketStatus.ANDAMENTO);
        }

        entity = ticketRepository.save(entity);
        return new TicketResponseDTO(entity);
    }

    public TicketResponseDTO cancelar(Long id, TicketCancelDTO dto, String emailAutor) {
        TicketEntity entity = findEntityById(id);
        validarTecnicoPodeAgir(entity, emailAutor);
        entity.setStatus(TicketStatus.CANCELADO);
        entity.setResolvedAt(LocalDateTime.now());
        entity = ticketRepository.save(entity);

        UsuarioEntity autor = usuarioService.findEntityByEmail(emailAutor);
        ComentarioEntity comentario = new ComentarioEntity();
        comentario.setMensagem("Chamado cancelado. Motivo: " + dto.motivo());
        comentario.setTicket(entity);
        comentario.setAutor(autor);
        comentarioRepository.save(comentario);

        return new TicketResponseDTO(entity);
    }

    // Bloqueia status/escalar/cancelar quando a pessoa NÃO é a responsável pelo chamado
    // E também não é um técnico do MESMO nível do chamado (comparação exata, sem herança).
    private void validarTecnicoPodeAgir(TicketEntity entity, String emailAutor) {
        UsuarioEntity tecnico = usuarioService.findEntityByEmail(emailAutor);

        boolean eOTecnicoResponsavel = entity.getTechnician() != null
                && entity.getTechnician().getId().equals(tecnico.getId());

        boolean nivelBateComOChamado = nivelDoRole(tecnico.getRole()) == entity.getCurrentLevel();

        if (!eOTecnicoResponsavel && !nivelBateComOChamado) {
            throw new IllegalStateException(
                    "Este chamado está no nível " + entity.getCurrentLevel()
                            + " e só pode ser alterado por um técnico desse nível ou pelo técnico responsável."
            );
        }
    }

    private SupportLevel nivelDoRole(UserRole role) {
        return switch (role) {
            case TECNICO_N1 -> SupportLevel.N1;
            case TECNICO_N2 -> SupportLevel.N2;
            case TECNICO_N3 -> SupportLevel.N3;
            case SOLICITANTE -> null;
        };
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