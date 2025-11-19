package com.imepac.checkin.repository;

import com.imepac.checkin.model.Coordenador;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CoordenadorRepository extends JpaRepository<Coordenador, Integer> {

    Optional<Coordenador> findByLogin(String login);
}