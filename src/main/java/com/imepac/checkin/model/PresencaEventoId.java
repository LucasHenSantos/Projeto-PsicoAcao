package com.imepac.checkin.model;

import java.io.Serializable;
import java.util.Objects;

public class PresencaEventoId implements Serializable {
    private Integer aluno;
    private Integer evento;
    public PresencaEventoId() {}
    public PresencaEventoId(Integer aluno, Integer evento) {
        this.aluno = aluno;
        this.evento = evento;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PresencaEventoId that = (PresencaEventoId) o;
        return Objects.equals(aluno, that.aluno) &&
                Objects.equals(evento, that.evento);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aluno, evento);
    }
}