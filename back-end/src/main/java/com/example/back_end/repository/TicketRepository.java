package com.example.back_end.repository;

import com.example.back_end.entity.TicketEntity;
import com.example.back_end.enums.SupportLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TicketRepository extends JpaRepository<TicketEntity, Long> {
    // Busca chamados filtrando pelo nível de suporte (N1, N2, N3)
    List<TicketEntity> findByCurrentLevel(SupportLevel currentLevel);
}

//att para supportLevel