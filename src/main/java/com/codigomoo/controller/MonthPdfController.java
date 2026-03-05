package com.codigomoo.controller;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

// OJO: Este es el import correcto para Spring, no java.net.http.HttpHeaders
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codigomoo.model.Activity;
import com.codigomoo.repository.ActivityRepository;
import com.codigomoo.security.SecurityUtils;
import com.codigomoo.service.PdfReportService;

@RestController
@RequestMapping("/api")
public class MonthPdfController {

    private final ActivityRepository repo;
    private final PdfReportService pdf;

    public MonthPdfController(ActivityRepository repo, PdfReportService pdf) {
        this.repo = repo;
        this.pdf = pdf;
    }

    @GetMapping("/months/{year}/{month}/pdf")
    public ResponseEntity<byte[]> download(@PathVariable Integer year, @PathVariable Integer month) {
        Long userId = SecurityUtils.currentUserId();

        // 1. Obtenemos la lista tipada correctamente
        List<Activity> activities = repo.listMonth(userId, year, month);

        // 2. Generamos el título bonito (Ej: "ENERO 2026")
        String mesTitulo = Month.of(month)
                .getDisplayName(TextStyle.FULL, new Locale("es", "MX"))
                .toUpperCase();// + " " + year;

        // 3. Llamamos al servicio que renderiza el HTML a PDF
        byte[] bytes = pdf.buildPlanPdf(userId, mesTitulo, activities);

        // 4. Nombre del archivo para descarga
        String filename = "plan_trabajo_" + year + "_" + String.format("%02d", month) + ".pdf";

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(bytes);
    }

    @GetMapping("/years/{year}/pdf")
    public ResponseEntity<byte[]> downloadYear(@PathVariable Integer year) {

        Long userId = SecurityUtils.currentUserId();
        List<Activity> all = repo.listYear(userId, year);
        byte[] bytes = pdf.buildYearPlanPdf(userId, year, all);

        String filename = "plan_trabajo_" + year + "_ANUAL.pdf";

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(bytes);
    }

}