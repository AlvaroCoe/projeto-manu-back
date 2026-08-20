package com.example.back_end.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "tb_equipamentos")
public class EquipamentoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome do equipamento é obrigatório")
    private String nome;

    @NotBlank(message = "O código do patrimônio é obrigatório")
    @Column(name = "codigo_patrimonio", unique = true)
    private String codigoPatrimonio;

    @NotBlank(message = "O setor é obrigatório")
    private String setor;

    public EquipamentoEntity() {
    }

    public EquipamentoEntity(Long id, String nome, String codigoPatrimonio, String setor) {
        this.id = id;
        this.nome = nome;
        this.codigoPatrimonio = codigoPatrimonio;
        this.setor = setor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCodigoPatrimonio() {
        return codigoPatrimonio;
    }

    public void setCodigoPatrimonio(String codigoPatrimonio) {
        this.codigoPatrimonio = codigoPatrimonio;
    }

    public String getSetor() {
        return setor;
    }

    public void setSetor(String setor) {
        this.setor = setor;
    }
}