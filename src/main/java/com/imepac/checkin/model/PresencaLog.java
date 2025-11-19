package com.imepac.checkin.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "presenca_log")
public class PresencaLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPresenca;

    @ManyToOne
    @JoinColumn(name = "id_aluno", nullable = false)
    private Aluno aluno;

    @ManyToOne
    @JoinColumn(name = "id_ponto", nullable = false)
    private CheckinPonto ponto;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    // 💡 NOVO CAMPO: Para registrar a hora da saída (Check-out).
    // nullable = true pois o check-out é opcional/posterior.
    @Column(name = "checkout_timestamp", nullable = true)
    private LocalDateTime checkOutTimestamp;

    // --- Getters e Setters ---

    public Integer getIdPresenca() {
        return idPresenca;
    }

    public void setIdPresenca(Integer idPresenca) {
        this.idPresenca = idPresenca;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public void setAluno(Aluno aluno) {
        this.aluno = aluno;
    }

    public CheckinPonto getPonto() {
        return ponto;
    }

    public void setPonto(CheckinPonto ponto) {
        this.ponto = ponto;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public LocalDateTime getCheckOutTimestamp() {
        return checkOutTimestamp;
    }

    public void setCheckOutTimestamp(LocalDateTime checkOutTimestamp) {
        this.checkOutTimestamp = checkOutTimestamp;
    }
}