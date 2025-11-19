package com.imepac.checkin.repository;

import com.imepac.checkin.model.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AlunoRepository extends JpaRepository<Aluno, Integer> {
    Optional<Aluno> findByMatricula(String matricula);
}