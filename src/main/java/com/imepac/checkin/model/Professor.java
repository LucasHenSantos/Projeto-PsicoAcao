package com.imepac.checkin.model;

import jakarta.persistence.*;

@Entity
@Table(name = "professor")
public class Professor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_professor")
    private Integer idProfessor;

    @Column(name = "nome", length = 150)
    private String nome;

    @Column(name = "email_corporativo", unique = true, length = 100)
    private String emailCorporativo;

    @Column(name = "login", unique = true, length = 100)
    private String login;

    @Column(name = "senha", length = 255)
    private String senha;
    public Integer getIdProfessor() {
        return idProfessor;
    }
    public void setIdProfessor(Integer idProfessor) {
        this.idProfessor = idProfessor;
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
}