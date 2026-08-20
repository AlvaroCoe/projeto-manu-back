package com.example.back_end.service;

import com.example.back_end.dto.EquipamentoCreateDTO;
import com.example.back_end.dto.EquipamentoResponseDTO;
import com.example.back_end.entity.EquipamentoEntity;
import com.example.back_end.exception.ResourceNotFoundException;
import com.example.back_end.repository.EquipamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EquipamentoService {

    @Autowired
    private EquipamentoRepository equipamentoRepository;

    public EquipamentoResponseDTO create(EquipamentoCreateDTO dto) {
        EquipamentoEntity entity = new EquipamentoEntity();
        entity.setNome(dto.nome());
        entity.setCodigoPatrimonio(dto.codigoPatrimonio());
        entity.setSetor(dto.setor());

        entity = equipamentoRepository.save(entity);
        return new EquipamentoResponseDTO(entity);
    }

    public List<EquipamentoResponseDTO> findAll() {
        return equipamentoRepository.findAll()
                .stream()
                .map(EquipamentoResponseDTO::new)
                .toList();
    }

    public EquipamentoResponseDTO findById(Long id) {
        return new EquipamentoResponseDTO(findEntityById(id));
    }

    // Usado internamente pelo TicketService ao vincular um equipamento ao chamado
    public EquipamentoEntity findEntityById(Long id) {
        return equipamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipamento não encontrado. Id: " + id));
    }
}
