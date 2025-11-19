package com.imepac.checkin.repository;

import com.imepac.checkin.model.PresencaLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // Importe
import java.util.List; // Importe

public interface PresencaLogRepository extends JpaRepository<PresencaLog, Integer> {

    @Query("SELECT p FROM PresencaLog p JOIN FETCH p.aluno JOIN FETCH p.ponto pt JOIN FETCH pt.evento")
    List<PresencaLog> findAllWithDetails();

}