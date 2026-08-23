package com.example.back_end.repository;

import com.example.back_end.entity.ComentarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ComentarioRepository extends JpaRepository<ComentarioEntity, Long> {
    List<ComentarioEntity> findByTicketIdOrderByCreatedAtAsc(Long ticketId);
}