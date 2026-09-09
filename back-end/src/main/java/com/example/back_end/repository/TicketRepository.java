package com.example.back_end.repository;

import com.example.back_end.entity.TicketEntity;
import com.example.back_end.enums.SupportLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TicketRepository extends JpaRepository<TicketEntity, Long> {
    List<TicketEntity> findByCurrentLevel(SupportLevel currentLevel);
    List<TicketEntity> findByClientId(Long clientId);

    // Usados pra impedir excluir um funcionário que já tem histórico
    boolean existsByClientId(Long clientId);
    boolean existsByTechnicianId(Long technicianId);
}