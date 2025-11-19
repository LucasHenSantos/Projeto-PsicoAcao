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
        Aluno aluno;
        Optional<Aluno> alunoOpt = alunoRepository.findByMatricula(matricula);

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
        PresencaLog log = new PresencaLog();
        log.setAluno(aluno);
        log.setPonto(pontoOpt.get());
        log.setTimestamp(LocalDateTime.now());
        presencaLogRepository.save(log);

        return "redirect:/checkin/sucesso";
    }
    @GetMapping("/checkin/sucesso")
    public String showSucesso() {
        return "checkin-sucesso";
    }
}