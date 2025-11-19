package com.imepac.checkin.repository;

import com.imepac.checkin.model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface EventoRepository extends JpaRepository<Evento, Integer> {

    List<Evento> findAllByVisivelTrue();
    List<Evento> findAllByVisivelFalse();

    List<Evento> findByDataBetween(LocalDateTime inicio, LocalDateTime fim);
    List<Evento> findByNomeContainingIgnoreCase(String nome);
}