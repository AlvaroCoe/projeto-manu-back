package com.example.back_end.repository;

import com.example.back_end.entity.PasswordResetTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetTokenEntity, Long> {
    Optional<PasswordResetTokenEntity> findByToken(String token);

    // Usado para invalidar links antigos e aplicar o cooldown entre solicitações
    List<PasswordResetTokenEntity> findByUsuarioIdAndUsadoFalse(Long usuarioId);
}