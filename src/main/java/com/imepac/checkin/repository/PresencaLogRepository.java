package com.imepac.checkin.repository;

import com.imepac.checkin.model.PresencaLog;
import com.imepac.checkin.model.Aluno;
import com.imepac.checkin.model.CheckinPonto;
import com.imepac.checkin.model.Evento; // 💡 NOVO IMPORT
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface PresencaLogRepository extends JpaRepository<PresencaLog, Integer> {

    @Query("SELECT p FROM PresencaLog p JOIN FETCH p.aluno JOIN FETCH p.ponto pt JOIN FETCH pt.evento")
    List<PresencaLog> findAllWithDetails();

    Optional<PresencaLog> findByAlunoAndPonto_EventoAndCheckOutTimestampIsNull(Aluno aluno, Evento evento);
}