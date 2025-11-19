package com.imepac.checkin.service;

import com.imepac.checkin.model.PresencaLog;
import com.imepac.checkin.repository.PresencaLogRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importe

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RelatorioService {

    @Autowired
    private PresencaLogRepository presencaLogRepository;
    @Transactional(readOnly = true)
    public void gerarRelatorio(HttpServletResponse response, LocalDate dataInicio, LocalDate dataFim, String nomeEvento) throws IOException {

        List<PresencaLog> todosOsLogs = presencaLogRepository.findAll();

        List<PresencaLog> logsFiltrados = todosOsLogs.stream()
                .filter(log -> {
                    if (log == null) return false;
                    if (log.getAluno() == null) return false;
                    if (log.getTimestamp() == null) return false;
                    if (log.getPonto() == null) return false;
                    if (log.getPonto().getEvento() == null) return false;

                    LocalDate logDate = log.getTimestamp().toLocalDate();

                    if (dataInicio != null && logDate.isBefore(dataInicio)) {
                        return false;
                    }
                    if (dataFim != null && logDate.isAfter(dataFim)) {
                        return false;
                    }

                    if (nomeEvento != null && !nomeEvento.trim().isEmpty()) {
                        String nomePalestra = log.getPonto().getEvento().getNome();
                        if (!nomePalestra.toLowerCase().contains(nomeEvento.toLowerCase())) {
                            return false;
                        }
                    }

                    return true;
                })
                .collect(Collectors.toList());

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Relatório de Presença");

        String[] headers = {"Nome Aluno", "Matrícula", "Nome Palestra", "Registro (Ponto)", "Data", "Hora"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        int rowNum = 1;
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        for (PresencaLog log : logsFiltrados) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(log.getAluno().getNomeCompleto());
            row.createCell(1).setCellValue(log.getAluno().getMatricula());
            row.createCell(2).setCellValue(log.getPonto().getEvento().getNome());
            row.createCell(3).setCellValue(log.getPonto().getNome());
            row.createCell(4).setCellValue(log.getTimestamp().format(dateFormatter));
            row.createCell(5).setCellValue(log.getTimestamp().format(timeFormatter));
        }
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"relatorio_presenca.xlsx\"");
        workbook.write(response.getOutputStream());
        workbook.close();
    }
}