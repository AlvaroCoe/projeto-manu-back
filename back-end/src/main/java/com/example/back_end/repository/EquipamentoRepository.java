package com.example.back_end.repository;

import com.example.back_end.entity.EquipamentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EquipamentoRepository extends JpaRepository<EquipamentoEntity, Long> {
    Optional<EquipamentoEntity> findByCodigoPatrimonio(String codigoPatrimonio);
}

//mudança no CodigoPratimonio