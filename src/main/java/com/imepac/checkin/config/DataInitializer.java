package com.imepac.checkin.config;

import com.imepac.checkin.model.Coordenador;
import com.imepac.checkin.repository.CoordenadorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private CoordenadorRepository coordenadorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {


        String adminLogin = "coordenador@imepac.edu.br";
        String adminSenha = "admin123";

        if (coordenadorRepository.findByLogin(adminLogin).isEmpty()) {

            System.out.println("Nenhum Coordenador encontrado, criando usuário padrão...");


            Coordenador admin = new Coordenador();
            admin.setNome("Coordenador Admin");
            admin.setLogin(adminLogin);
            admin.setEmailCorporativo(adminLogin);

            admin.setSenha(passwordEncoder.encode(adminSenha));

            coordenadorRepository.save(admin);

            System.out.println("Usuário Coordenador criado com sucesso!");
        } else {
            System.out.println("Usuário Coordenador já existe.");
        }
    }
}