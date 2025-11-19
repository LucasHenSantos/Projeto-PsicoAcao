package com.imepac.checkin.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "evento")
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evento")
    private Integer idEvento;

    @Column(name = "nome", length = 300)
    private String nome;

    @Column(name = "data")
    private LocalDateTime data;

    @Column(name = "quantidade_pausas")
    private Integer quantidadePausas;
    @Column(name = "visivel")
    private boolean visivel = true;
    @OneToMany(mappedBy = "evento")
    private Set<CheckinPonto> pontos;

    public Integer getIdEvento() { return idEvento; }
    public void setIdEvento(Integer idEvento) { this.idEvento = idEvento; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public LocalDateTime getData() { return data; }
    public void setData(LocalDateTime data) { this.data = data; }
    public Integer getQuantidadePausas() { return quantidadePausas; }
    public void setQuantidadePausas(Integer quantidadePausas) { this.quantidadePausas = quantidadePausas; }
    public Set<CheckinPonto> getPontos() { return pontos; }
    public void setPontos(Set<CheckinPonto> pontos) { this.pontos = pontos; }
    public boolean isVisivel() {
        return visivel;
    }

    public void setVisivel(boolean visivel) {
        this.visivel = visivel;
    }
}