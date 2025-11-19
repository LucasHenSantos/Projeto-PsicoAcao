package com.imepac.checkin.controller;

import com.imepac.checkin.model.Aluno;
import com.imepac.checkin.model.CheckinPonto;
import com.imepac.checkin.model.PresencaLog;
import com.imepac.checkin.repository.AlunoRepository;
import com.imepac.checkin.repository.CheckinPontoRepository;
import com.imepac.checkin.repository.PresencaLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
public class CheckinController {

    @Autowired private AlunoRepository alunoRepository;
    @Autowired private CheckinPontoRepository pontoRepository;
    @Autowired private PresencaLogRepository presencaLogRepository;

    @GetMapping("/checkin/{uuid}")
    public String showFormularioCheckin(@PathVariable("uuid") String uuid, Model model) {

        Optional<CheckinPonto> pontoOpt = pontoRepository.findByUuid(uuid);

        if (pontoOpt.isEmpty()) {
            model.addAttribute("erro", "QR Code inválido ou expirado.");
        } else {
            model.addAttribute("ponto", pontoOpt.get());
        }
        if (!model.containsAttribute("nome")) {
            model.addAttribute("nome", "");
        }
        if (!model.containsAttribute("matricula")) {
            model.addAttribute("matricula", "");
        }

        return "checkin-form";
    }

    @PostMapping("/checkin/processar")
    public String processarCheckin(@RequestParam("uuid") String uuid,
                                   @RequestParam("matricula") String matricula,
                                   @RequestParam("nome") String nome,
                                   RedirectAttributes redirectAttributes) {

        Optional<CheckinPonto> pontoOpt = pontoRepository.findByUuid(uuid);
        if (pontoOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("erro", "QR Code inválido. Tente escanear novamente.");
            redirectAttributes.addFlashAttribute("nome", nome);
            redirectAttributes.addFlashAttribute("matricula", matricula);
            return "redirect:/checkin/" + uuid;
        }

        // 1. Lógica de busca/criação do Aluno (Seu código original)
        Aluno aluno;
        Optional<Aluno> alunoOpt = alunoRepository.findByMatricula(matricula);

        // ... (Lógica de Aluno inalterada: busca ou cria)
        if (alunoOpt.isPresent()) {
            Aluno alunoExistente = alunoOpt.get();
            if (alunoExistente.getNomeCompleto().trim().equalsIgnoreCase(nome.trim())) {
                aluno = alunoExistente;
            } else {
                redirectAttributes.addFlashAttribute("erro",
                        "Esta matrícula (" + matricula + ") já está em uso por outro aluno: '" +
                                alunoExistente.getNomeCompleto() + "'. Verifique seus dados.");
                redirectAttributes.addFlashAttribute("nome", nome);
                redirectAttributes.addFlashAttribute("matricula", matricula);
                return "redirect:/checkin/" + uuid;
            }
        } else {
            aluno = new Aluno();
            aluno.setMatricula(matricula);
            aluno.setNomeCompleto(nome.trim());

            try {
                alunoRepository.save(aluno);
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("erro", "Erro ao salvar aluno. Tente novamente.");
                redirectAttributes.addFlashAttribute("nome", nome);
                redirectAttributes.addFlashAttribute("matricula", matricula);
                return "redirect:/checkin/" + uuid;
            }
        }

        // 2. Lógica de Check-in ou Check-out (NOVA LÓGICA)
        CheckinPonto ponto = pontoOpt.get();
        String tipoOperacao;

        // Busca um log de presença que o aluno fez neste ponto e que AINDA NÃO TEM check-out
        Optional<PresencaLog> logOpt = presencaLogRepository.findByAlunoAndPontoAndCheckOutTimestampIsNull(aluno, ponto);

        if (logOpt.isPresent()) {
            // Se encontrar um log, realiza o CHECK-OUT: atualiza o timestamp de saída
            PresencaLog log = logOpt.get();
            log.setCheckOutTimestamp(LocalDateTime.now()); // <--- Define a hora de saída
            presencaLogRepository.save(log);
            tipoOperacao = "checkout";

        } else {
            // Se não encontrar, realiza o CHECK-IN: cria um novo log
            PresencaLog novoLog = new PresencaLog();
            novoLog.setAluno(aluno);
            novoLog.setPonto(ponto);
            novoLog.setTimestamp(LocalDateTime.now()); // Este é o CheckInTimestamp
            // novoLog.setCheckOutTimestamp(null); <--- Deixa o check-out nulo
            presencaLogRepository.save(novoLog);
            tipoOperacao = "checkin";
        }

        // 3. Redireciona para a tela de sucesso, passando o tipo de sucesso via URL
        return "redirect:/checkin/sucesso?tipo=" + tipoOperacao; // <--- Redirecionamento Dinâmico
    }

    @GetMapping("/checkin/sucesso")
    public String showSucesso(@RequestParam(name = "tipo", required = false, defaultValue = "checkin") String tipo, Model model) {

        // Adiciona o parâmetro 'tipo' ao modelo para ser usado pelo Thymeleaf no HTML
        model.addAttribute("tipoSucesso", tipo);

        return "checkin-sucesso";
    }
}