package com.example.back_end.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_password_reset_tokens")
public class PasswordResetTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String token;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private UsuarioEntity usuario;

    private LocalDateTime dataCriacao;
    private LocalDateTime dataExpiracao;

    // DEFAULT 0 garante que registros antigos (se a coluna for adicionada depois) não fiquem NULL
    @Column(columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean usado = false;

    public PasswordResetTokenEntity() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public UsuarioEntity getUsuario() { return usuario; }
    public void setUsuario(UsuarioEntity usuario) { this.usuario = usuario; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public LocalDateTime getDataExpiracao() { return dataExpiracao; }
    public void setDataExpiracao(LocalDateTime dataExpiracao) { this.dataExpiracao = dataExpiracao; }

    public Boolean getUsado() { return usado; }
    public void setUsado(Boolean usado) { this.usado = usado; }
}