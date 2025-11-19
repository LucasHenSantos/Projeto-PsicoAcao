package com.imepac.checkin.service;

import com.imepac.checkin.model.Aluno;
import com.imepac.checkin.model.CheckinPonto;
import com.imepac.checkin.model.PresencaLog;
import com.imepac.checkin.repository.PresencaLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CheckinService {

    @Autowired
    private PresencaLogRepository presencaLogRepository;

    private static final String PONTO_TYPE_SAIDA = "saída";
    private static final String PONTO_TYPE_ENTRADA = "entrada";

    private String getPontoType(CheckinPonto ponto) {
        String nome = ponto.getNome().toLowerCase();
        // Assume que o ponto é de ENTRADA se tiver "check-in" ou "volta"
        if (nome.contains("check-in") || nome.contains("volta")) {
            return PONTO_TYPE_ENTRADA;
        }
        // Assume que o ponto é de SAÍDA se tiver "checkout" ou "saída"
        if (nome.contains("checkout") || nome.contains("saída")) {
            return PONTO_TYPE_SAIDA;
        }
        return "desconhecido";
    }

    @Transactional
    public String registrarPonto(Aluno aluno, CheckinPonto ponto) {

        String tipoPonto = getPontoType(ponto);

        // 1. Busca o log aberto em TODO O EVENTO
        Optional<PresencaLog> logAbertoOpt = presencaLogRepository.findByAlunoAndPonto_EventoAndCheckOutTimestampIsNull(aluno, ponto.getEvento());

        if (tipoPonto.equals(PONTO_TYPE_ENTRADA)) {
            if (logAbertoOpt.isPresent()) {
                // Aluno já está ativo. Não cria um novo log.
                return "aviso_ja_logado";
            }
            // 1. AÇÃO: CRIAR NOVO CHECK-IN
            PresencaLog novoLog = new PresencaLog();
            novoLog.setAluno(aluno);
            novoLog.setPonto(ponto);
            novoLog.setTimestamp(LocalDateTime.now());
            presencaLogRepository.save(novoLog);
            return "checkin";
        }

        if (tipoPonto.equals(PONTO_TYPE_SAIDA)) {
            if (logAbertoOpt.isPresent()) {
                // 2. AÇÃO: FAZER CHECK-OUT
                PresencaLog log = logAbertoOpt.get();
                log.setCheckOutTimestamp(LocalDateTime.now());
                presencaLogRepository.save(log);
                return "checkout";
            } else {
                // 3. AÇÃO: ERRO CRÍTICO - Tentando fazer Check-out sem Check-in
                return "erro_checkin_pendente";
            }
        }

        // 4. AÇÃO: PONTO DESCONHECIDO
        return "erro_ponto_desconhecido";
    }
}