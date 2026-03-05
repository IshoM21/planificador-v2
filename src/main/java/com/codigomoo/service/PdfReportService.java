package com.codigomoo.service;

import java.io.ByteArrayOutputStream;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.codigomoo.dto.MonthSection;
import com.codigomoo.exception.ApiError;
import com.codigomoo.model.Activity;
import com.codigomoo.model.Theme;
import com.codigomoo.repository.UserProfileRepository;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

@Service
public class PdfReportService {

    private final SpringTemplateEngine templateEngine;
    private final UserProfileRepository profileRepo;

    public PdfReportService(SpringTemplateEngine templateEngine,
            UserProfileRepository profileRepo) {
        this.templateEngine = templateEngine;
        this.profileRepo = profileRepo;
    }

    /**
     * Genera PDF mensual usando plantilla según el theme del usuario.
     */
    public byte[] buildPlanPdf(Long userId, String mesTitulo, List<Activity> activities) {
        Theme theme = profileRepo.findById(userId)
                .orElseThrow(() -> new ApiError(HttpStatus.NOT_FOUND, "Profile not found"))
                .getTheme();

        String templateName = resolveMonthlyTemplate(theme);

        Context context = new Context();
        context.setVariable("nombreMes", mesTitulo);
        context.setVariable("actividades", activities);

        String htmlContent = templateEngine.process(templateName, context);

        return htmlToPdf(htmlContent, "Error al generar el PDF del plan mensual");
    }

    /**
     * Genera PDF anual usando plantilla según el theme del usuario.
     */
    public byte[] buildYearPlanPdf(Long userId, Integer year, List<Activity> allActivities) {
        Theme theme = profileRepo.findById(userId)
                .orElseThrow(() -> new ApiError(HttpStatus.NOT_FOUND, "Profile not found"))
                .getTheme();

        String templateName = resolveYearlyTemplate(theme);

        // 1) Agrupar por mes (1-12)
        Map<Integer, List<Activity>> byMonth = allActivities.stream()
                .collect(Collectors.groupingBy(Activity::getMonth, TreeMap::new, Collectors.toList()));

        // 2) Armar 12 secciones (aunque no haya actividades)
        Locale esMX = new Locale("es", "MX");
        List<MonthSection> months = IntStream.rangeClosed(1, 12)
                .mapToObj(m -> {
                    String titulo = Month.of(m).getDisplayName(TextStyle.FULL, esMX).toUpperCase();
                    List<Activity> list = byMonth.getOrDefault(m, List.of());
                    return new MonthSection(m, titulo, list);
                })
                .toList();

        // 3) Thymeleaf
        Context context = new Context();
        context.setVariable("year", year);
        context.setVariable("months", months);

        String htmlContent = templateEngine.process(templateName, context);

        return htmlToPdf(htmlContent, "Error al generar el PDF anual");
    }

    // -----------------------
    // Helpers
    // -----------------------

    private byte[] htmlToPdf(String htmlContent, String errorMessage) {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(htmlContent, "");
            builder.toStream(os);
            builder.run();
            return os.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(errorMessage, e);
        }
    }

    private String resolveMonthlyTemplate(Theme theme) {
        return switch (theme) {
            case CONQUISTADORES -> "reporte_conquistadores";
            case AVENTUREROS -> "reporte_aventureros";
            case GUIAS_MAYORES -> "reporte_guias_mayores";
        };
    }

    private String resolveYearlyTemplate(Theme theme) {
        return switch (theme) {
            case CONQUISTADORES -> "reporte_conquistadores_anual";
            case AVENTUREROS -> "reporte_aventureros_anual";
            case GUIAS_MAYORES -> "reporte_guias_mayores_anual";
        };
    }
}
