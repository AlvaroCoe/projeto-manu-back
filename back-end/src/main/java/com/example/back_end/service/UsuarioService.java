package com.example.back_end.service;

import com.example.back_end.dto.UsuarioCreateDTO;
import com.example.back_end.dto.UsuarioResponseDTO;
import com.example.back_end.dto.UsuarioStatusUpdateDTO;
import com.example.back_end.dto.UsuarioUpdateDTO;
import com.example.back_end.entity.UsuarioEntity;
import com.example.back_end.exception.ResourceNotFoundException;
import com.example.back_end.repository.ComentarioRepository;
import com.example.back_end.repository.TicketRepository;
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

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private ComentarioRepository comentarioRepository;

    public UsuarioResponseDTO create(UsuarioCreateDTO dto) {
        UsuarioEntity entity = new UsuarioEntity();
        entity.setNome(dto.nome());
        entity.setEmail(dto.email());
        entity.setSenha(passwordEncoder.encode(dto.senha()));
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

    public UsuarioEntity findEntityById(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado. Id: " + id));
    }

    public UsuarioEntity findEntityByEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + email));
    }

    // Edita nome, e-mail e papel de um funcionário já existente
    public UsuarioResponseDTO update(Long id, UsuarioUpdateDTO dto) {
        UsuarioEntity entity = findEntityById(id);
        entity.setNome(dto.nome());
        entity.setEmail(dto.email());
        entity.setRole(dto.role());

        entity = usuarioRepository.save(entity);
        return new UsuarioResponseDTO(entity);
    }

    // Ativar ("admitir de volta") ou desativar ("descadastrar") sem apagar nada
    public UsuarioResponseDTO atualizarStatus(Long id, UsuarioStatusUpdateDTO dto) {
        UsuarioEntity entity = findEntityById(id);
        entity.setAtivo(dto.ativo());

        entity = usuarioRepository.save(entity);
        return new UsuarioResponseDTO(entity);
    }

    // Exclusão definitiva — só permitida se o funcionário nunca teve chamado/comentário associado
    public void deletar(Long id) {
        UsuarioEntity entity = findEntityById(id);

        boolean temChamados = ticketRepository.existsByClientId(id) || ticketRepository.existsByTechnicianId(id);
        boolean temComentarios = comentarioRepository.existsByAutorId(id);

        if (temChamados || temComentarios) {
            throw new IllegalStateException(
                    "Não é possível excluir este usuário: ele possui chamados ou comentários associados. " +
                            "Desative a conta em vez de excluir, para preservar o histórico de atendimento."
            );
        }

        usuarioRepository.delete(entity);
    }
}