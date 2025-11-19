package com.imepac.checkin.controller;

import com.imepac.checkin.model.Aluno;
import com.imepac.checkin.model.CheckinPonto;
import com.imepac.checkin.repository.AlunoRepository;
import com.imepac.checkin.repository.CheckinPontoRepository;
import com.imepac.checkin.service.CheckinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class CheckinController {

    @Autowired private AlunoRepository alunoRepository;
    @Autowired private CheckinPontoRepository pontoRepository;
    @Autowired private CheckinService checkinService;

    @GetMapping("/checkin/{uuid}")
    public String showFormularioCheckin(@PathVariable("uuid") String uuid, Model model) {
        // ... (CÓDIGO INALTERADO)
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

        CheckinPonto ponto = pontoOpt.get();
        String tipoOperacao = checkinService.registrarPonto(aluno, ponto);

        if (tipoOperacao.startsWith("erro_") || tipoOperacao.startsWith("aviso_")) {
            String mensagem;
            if (tipoOperacao.equals("erro_checkin_pendente")) {
                mensagem = "Erro: Não é possível registrar Saída. Você não possui um Check-in de Entrada ativo para este evento.";
            } else if (tipoOperacao.equals("aviso_ja_logado")) {
                mensagem = "Aviso: Você já está registrado como presente neste evento. Não é possível fazer um novo Check-in/Volta.";
            } else if (tipoOperacao.equals("erro_ponto_desconhecido")) {
                mensagem = "Erro: O ponto escaneado não é reconhecido como entrada ou saída.";
            } else {
                mensagem = "Erro: Problema desconhecido na validação do ponto.";
            }

            redirectAttributes.addFlashAttribute("erro", mensagem);
            redirectAttributes.addFlashAttribute("nome", nome);
            redirectAttributes.addFlashAttribute("matricula", matricula);
            return "redirect:/checkin/" + uuid;
        }

        return "redirect:/checkin/sucesso?tipo=" + tipoOperacao;
    }

    @GetMapping("/checkin/sucesso")
    public String showSucesso(@RequestParam(name = "tipo", required = false, defaultValue = "checkin") String tipo, Model model) {
        model.addAttribute("tipoSucesso", tipo);
        return "checkin-sucesso";
    }
}