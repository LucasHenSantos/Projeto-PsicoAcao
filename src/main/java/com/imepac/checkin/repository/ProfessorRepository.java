package com.imepac.checkin.repository;

import com.imepac.checkin.model.Professor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ProfessorRepository extends JpaRepository<Professor, Integer> {

    Optional<Professor> findByLogin(String login);
}