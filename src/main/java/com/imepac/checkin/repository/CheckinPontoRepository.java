package com.imepac.checkin.repository;

import com.imepac.checkin.model.CheckinPonto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CheckinPontoRepository extends JpaRepository<CheckinPonto, Integer> {

    List<CheckinPonto> findByEventoIdEvento(Integer eventoId);

    Optional<CheckinPonto> findByUuid(String uuid);
}