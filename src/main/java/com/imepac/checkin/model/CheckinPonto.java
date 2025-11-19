package com.imepac.checkin.model;

import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "checkin_ponto")
public class CheckinPonto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPonto;

    @ManyToOne
    @JoinColumn(name = "id_evento", nullable = false)
    private Evento evento;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "uuid", unique = true, nullable = false)
    private String uuid;
    @OneToMany(mappedBy = "ponto")
    private Set<PresencaLog> logs;
    public Integer getIdPonto() { return idPonto; }
    public void setIdPonto(Integer idPonto) { this.idPonto = idPonto; }
    public Evento getEvento() { return evento; }
    public void setEvento(Evento evento) { this.evento = evento; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getUuid() { return uuid; }
    public void setUuid(String uuid) { this.uuid = uuid; }
    public Set<PresencaLog> getLogs() { return logs; }
    public void setLogs(Set<PresencaLog> logs) { this.logs = logs; }
}