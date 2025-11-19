package com.imepac.checkin.controller;


import com.imepac.checkin.model.CheckinPonto;
import com.imepac.checkin.model.Evento;
import com.imepac.checkin.model.Professor;
import com.imepac.checkin.repository.CheckinPontoRepository;
import com.imepac.checkin.repository.EventoRepository;
import com.imepac.checkin.repository.ProfessorRepository;
import com.imepac.checkin.service.QrCodeService;
import com.imepac.checkin.service.RelatorioService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/admin")
public class AdminController {


    @Autowired private EventoRepository eventoRepository;
    @Autowired private CheckinPontoRepository pontoRepository;
    @Autowired private QrCodeService qrCodeService;
    @Autowired private RelatorioService relatorioService;
    @Autowired private ProfessorRepository professorRepository;
    @Autowired private PasswordEncoder passwordEncoder;


    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        model.addAttribute("eventos", eventoRepository.findAllByVisivelTrue());
        return "admin-dashboard";
    }

    @GetMapping("/eventos/novo")
    public String showFormularioEvento(Model model) {
        model.addAttribute("evento", new Evento());
        return "evento-form";
    }

    @PostMapping("/eventos/salvar")
    public String salvarEvento(Evento evento, RedirectAttributes redirectAttributes) {
        Evento eventoSalvo = eventoRepository.save(evento);
        criarPontosDeCheckin(eventoSalvo);
        redirectAttributes.addFlashAttribute("sucesso", "Evento criado com sucesso!");
        return "redirect:/admin/evento/" + eventoSalvo.getIdEvento() + "/controlar";
    }

    private void criarPontosDeCheckin(Evento evento) {
        CheckinPonto inicial = new CheckinPonto();
        inicial.setEvento(evento);
        inicial.setNome("Check-in Inicial");
        inicial.setUuid(UUID.randomUUID().toString());
        pontoRepository.save(inicial);

        if (evento.getQuantidadePausas() != null) {
            for (int i = 1; i <= evento.getQuantidadePausas(); i++) {
                CheckinPonto saidaPausa = new CheckinPonto();
                saidaPausa.setEvento(evento);
                saidaPausa.setNome("Saída Pausa " + i);
                saidaPausa.setUuid(UUID.randomUUID().toString());
                pontoRepository.save(saidaPausa);

                CheckinPonto voltaPausa = new CheckinPonto();
                voltaPausa.setEvento(evento);
                voltaPausa.setNome("Volta Pausa " + i);
                voltaPausa.setUuid(UUID.randomUUID().toString());
                pontoRepository.save(voltaPausa);
            }
        }

        CheckinPonto checkoutFinal = new CheckinPonto();
        checkoutFinal.setEvento(evento);
        checkoutFinal.setNome("Checkout Final");
        checkoutFinal.setUuid(UUID.randomUUID().toString());
        pontoRepository.save(checkoutFinal);
    }

    @GetMapping("/evento/{id}/controlar")
    public String controlarEvento(@PathVariable("id") Integer id, Model model) {
        Evento evento = eventoRepository.findById(id).get();
        model.addAttribute("evento", evento);
        List<CheckinPonto> pontos = pontoRepository.findByEventoIdEvento(id);
        model.addAttribute("pontos", pontos);
        return "evento-controlar";
    }

    @GetMapping("/qrcode/{uuid}")
    public String showQrCodePage(@PathVariable("uuid") String uuid, Model model) {
        CheckinPonto ponto = pontoRepository.findByUuid(uuid).orElse(null);
        if (ponto == null) {
            return "redirect:/admin/dashboard";
        }
        String checkinUrl = "http://192.168.100.10:8080/checkin/" + uuid;
        String qrCodeBase64 = qrCodeService.generateQRCodeBase64(checkinUrl, 300, 300);
        model.addAttribute("ponto", ponto);
        model.addAttribute("qrCodeBase64", qrCodeBase64);
        return "evento-qrcode";
    }

    @GetMapping("/relatorios/gerar")
    public void gerarRelatorio(HttpServletResponse response,
                               @RequestParam(required = false) String dataInicio,
                               @RequestParam(required = false) String dataFim,
                               @RequestParam(required = false) String nomeEvento) throws IOException {

        LocalDate inicio = (dataInicio == null || dataInicio.isEmpty()) ? null : LocalDate.parse(dataInicio);
        LocalDate fim = (dataFim == null || dataFim.isEmpty()) ? null : LocalDate.parse(dataFim);
        relatorioService.gerarRelatorio(response, inicio, fim, nomeEvento);
    }

    @GetMapping("/professores/novo")
    public String showFormularioProfessor(Model model) {
        model.addAttribute("professor", new Professor());
        return "professor-form";
    }

    @PostMapping("/professores/salvar")
    public String salvarProfessor(Professor professor, RedirectAttributes redirectAttributes) {
        professor.setSenha(passwordEncoder.encode(professor.getSenha()));
        professorRepository.save(professor);
        redirectAttributes.addFlashAttribute("sucesso", "Professor criado com sucesso!");
        return "redirect:/admin/dashboard";
    }


    @PostMapping("/evento/arquivar")
    public String arquivarEvento(@RequestParam("id") Integer id, RedirectAttributes redirectAttributes) {

        Optional<Evento> eventoOpt = eventoRepository.findById(id);

        if (eventoOpt.isPresent()) {
            Evento evento = eventoOpt.get();
            evento.setVisivel(false);
            eventoRepository.save(evento);
            redirectAttributes.addFlashAttribute("sucesso", "Evento arquivado com sucesso!");
        } else {
            redirectAttributes.addFlashAttribute("erro", "Erro: Evento não encontrado.");
        }

        return "redirect:/admin/dashboard";
    }

    @GetMapping("/eventos/arquivados")
    public String showEventosArquivados(Model model) {

        model.addAttribute("eventos", eventoRepository.findAllByVisivelFalse());

        return "eventos-arquivados";
    }



    @PostMapping("/evento/desarquivar")
    public String desarquivarEvento(@RequestParam("id") Integer id, RedirectAttributes redirectAttributes) {

        Optional<Evento> eventoOpt = eventoRepository.findById(id);

        if (eventoOpt.isPresent()) {
            Evento evento = eventoOpt.get();
            evento.setVisivel(true);
            eventoRepository.save(evento);
            redirectAttributes.addFlashAttribute("sucesso", "Evento restaurado com sucesso!");
        } else {
            redirectAttributes.addFlashAttribute("erro", "Erro: Evento não encontrado.");
        }

        return "redirect:/admin/eventos/arquivados";
    }
}