package com.imepac.checkin.service;

import com.imepac.checkin.model.Coordenador;
import com.imepac.checkin.model.Professor;
import com.imepac.checkin.repository.CoordenadorRepository;
import com.imepac.checkin.repository.ProfessorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class AppUserDetailsService implements UserDetailsService {

    @Autowired
    private CoordenadorRepository coordenadorRepository;

    @Autowired
    private ProfessorRepository professorRepository;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Optional<Coordenador> coordenadorOpt = coordenadorRepository.findByLogin(login);
        if (coordenadorOpt.isPresent()) {
            Coordenador coordenador = coordenadorOpt.get();
            return User.builder()
                    .username(coordenador.getLogin())
                    .password(coordenador.getSenha())
                    .authorities(Collections.singletonList(() -> "ROLE_COORDENADOR")) // Papel de Coordenador
                    .build();
        }
        Optional<Professor> professorOpt = professorRepository.findByLogin(login);
        if (professorOpt.isPresent()) {
            Professor professor = professorOpt.get();
            return User.builder()
                    .username(professor.getLogin())
                    .password(professor.getSenha())
                    .authorities(Collections.singletonList(() -> "ROLE_PROFESSOR")) // Papel de Professor
                    .build();
        }

        throw new UsernameNotFoundException("Usuário (Coordenador ou Professor) não encontrado: " + login);
    }
}