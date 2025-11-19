package com.imepac.checkin.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "coordenador")

public class Coordenador {
    public Integer getIdCoordenador() {
        return idCoordenador;
    }

    public void setIdCoordenador(Integer idCoordenador) {
        this.idCoordenador = idCoordenador;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmailCorporativo() {
        return emailCorporativo;
    }

    public void setEmailCorporativo(String emailCorporativo) {
        this.emailCorporativo = emailCorporativo;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_coordenador")
    private Integer idCoordenador;

    @Column(name = "nome", length = 100)
    private String nome;

    @Column(name = "email_corporativo", unique = true, length = 100)
    private String emailCorporativo; // Vamos usar este como login

    @Column(name = "login", unique = true, length = 50)
    private String login; // Ou este, se preferir

    @Column(name = "senha", length = 255)
    private String senha;
}