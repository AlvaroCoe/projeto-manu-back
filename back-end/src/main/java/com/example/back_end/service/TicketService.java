package com.example.back_end.service;

import com.example.back_end.dto.*;
import com.example.back_end.entity.ComentarioEntity;
import com.example.back_end.entity.EquipamentoEntity;
import com.example.back_end.entity.TicketEntity;
import com.example.back_end.entity.UsuarioEntity;
import com.example.back_end.enums.SupportLevel;
import com.example.back_end.enums.TicketStatus;
import com.example.back_end.exception.ResourceNotFoundException;
import com.example.back_end.repository.ComentarioRepository;
import com.example.back_end.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.back_end.enums.UserRole;

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

    public TicketResponseDTO updateStatus(Long id, TicketStatusUpdateDTO dto, String emailAutor) {
        TicketEntity entity = findEntityById(id);
        validarTecnicoPodeAgir(entity, emailAutor);
        entity.setStatus(dto.status());

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

        if (dto.technicianId() != null) {
            UsuarioEntity tecnico = usuarioService.findEntityById(dto.technicianId());
            entity.setTechnician(tecnico);
        }

        entity = ticketRepository.save(entity);
        UsuarioEntity autor = usuarioService.findEntityByEmail(emailAutor);
        ComentarioEntity comentario = new ComentarioEntity();
        comentario.setMensagem("Chamado escalado de " + nivelAnterior + " para " + proximoNivel + ". Motivo " + dto.motivo());
        comentario.setTicket(entity);
        comentario.setAutor(autor);
        comentarioRepository.save(comentario);
        return new TicketResponseDTO(entity);
    }

    // Técnico "assume" o chamado pra si. Se ainda estiver ABERTO, já passa pra ANDAMENTO.
    public TicketResponseDTO pegar(Long id, String emailTecnico) {
        TicketEntity entity = findEntityById(id);
        UsuarioEntity tecnico = usuarioService.findEntityByEmail(emailTecnico);

        entity.setTechnician(tecnico);
        if (entity.getStatus() == TicketStatus.ABERTO) {
            entity.setStatus(TicketStatus.ANDAMENTO);
        }

        entity = ticketRepository.save(entity);
        return new TicketResponseDTO(entity);
    }

    // Cancela o chamado e já registra o motivo como um comentário no histórico
    public TicketResponseDTO cancelar(Long id, TicketCancelDTO dto, String emailAutor) {
        TicketEntity entity = findEntityById(id);
        validarTecnicoPodeAgir(entity,emailAutor);
        entity.setStatus(TicketStatus.CANCELADO);
        entity = ticketRepository.save(entity);

        UsuarioEntity autor = usuarioService.findEntityByEmail(emailAutor);
        ComentarioEntity comentario = new ComentarioEntity();
        comentario.setMensagem("Chamado cancelado. Motivo: " + dto.motivo());
        comentario.setTicket(entity);
        comentario.setAutor(autor);
        comentarioRepository.save(comentario);

        return new TicketResponseDTO(entity);
    }
//Bloqueia escalar/finalizar/cancelar se o técnico não for do nível atual do chamado nem o responsável por ele

    private void validarTecnicoPodeAgir(TicketEntity entity, String emailAutor){
        UsuarioEntity tecnico = usuarioService.findEntityByEmail(emailAutor);
        boolean eOTecnicoResponsavel = entity.getTechnician() != null
                && entity.getTechnician().getId().equals(tecnico.getId());

        boolean estaNoNivelDoChamado = nivelDoRole(tecnico.getRole()) == entity.getCurrentLevel();

        if (eOTecnicoResponsavel && !estaNoNivelDoChamado){
            throw  new IllegalStateException(
                    "Este chamado está no nível " + entity.getCurrentLevel()
                    + " e só poderá ser alterado por um técnico desse nível ou pelo técnico responsável."
                    + " Você ainda pode comentar no chamado!"
            );
        }
    }

    private SupportLevel nivelDoRole(UserRole role){
        return switch (role){
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
