package com.example.back_end.entity;

import com.example.back_end.enums.Prioridade;
import com.example.back_end.enums.SupportLevel;
import com.example.back_end.enums.TicketStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_tickets")
public class TicketEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String descricao;

    @Enumerated(EnumType.STRING)
    private Prioridade prioridade;

    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    @Enumerated(EnumType.STRING)
    private SupportLevel currentLevel;

    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private UsuarioEntity client;

    @ManyToOne
    @JoinColumn(name = "technician_id")
    private UsuarioEntity technician;

    @ManyToOne
    @JoinColumn(name = "equipment_id")
    private EquipamentoEntity equipamento;

    public TicketEntity() {
        this.createdAt = LocalDateTime.now();
        this.status = TicketStatus.ABERTO;
        this.currentLevel = SupportLevel.N1;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public Prioridade getPrioridade() { return prioridade; }
    public void setPrioridade(Prioridade prioridade) { this.prioridade = prioridade; }
    public TicketStatus getStatus() { return status; }
    public void setStatus(TicketStatus status) { this.status = status; }
    public SupportLevel getCurrentLevel() { return currentLevel; }
    public void setCurrentLevel(SupportLevel currentLevel) { this.currentLevel = currentLevel; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }
    public UsuarioEntity getClient() { return client; }
    public void setClient(UsuarioEntity client) { this.client = client; }
    public UsuarioEntity getTechnician() { return technician; }
    public void setTechnician(UsuarioEntity technician) { this.technician = technician; }
    public EquipamentoEntity getEquipamento() { return equipamento; }
    public void setEquipamento(EquipamentoEntity equipamento) { this.equipamento = equipamento; }
}