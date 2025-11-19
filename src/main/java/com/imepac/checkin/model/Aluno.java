package com.imepac.checkin.model;

import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "aluno")
public class Aluno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_aluno")
    private Integer idAluno;

    @Column(name = "nome_completo", length = 100)
    private String nomeCompleto;

    @Column(name = "matricula", unique = true, length = 20)
    private String matricula;

    @OneToMany(mappedBy = "aluno")
    private Set<PresencaLog> presencas;


    public Integer getIdAluno() {
        return idAluno;
    }

    public void setIdAluno(Integer idAluno) {
        this.idAluno = idAluno;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public Set<PresencaLog> getPresencas() {
        return presencas;
    }

    public void setPresencas(Set<PresencaLog> presencas) {
        this.presencas = presencas;
    }
}