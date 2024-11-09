package com.AMIR.SRM.controllers;

import com.AMIR.SRM.domain.Order;
import com.AMIR.SRM.domain.PastOrder;
import com.AMIR.SRM.repositories.PastOrderRepo;
import com.AMIR.SRM.service.PdfGeneratorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

@Controller
public class PdfExportController {
    private final PdfGeneratorService pdfGeneratorService;
    private final PastOrderRepo pastOrderRepo;

    public PdfExportController(PdfGeneratorService pdfGeneratorService, PastOrderRepo pastOrderRepo ) {
        this.pdfGeneratorService = pdfGeneratorService;
        this.pastOrderRepo = pastOrderRepo;
    }

    @GetMapping("/pdf/generate/{order}")
    public void generatePDF(@PathVariable Order order, Model model, HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd:hh:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=Dogovor " + order.getId() + ".pdf";
        response.setHeader(headerKey, headerValue);

        this.pdfGeneratorService.export(response, order);
    }

    @GetMapping("/pdf/generate_past/{orderId}")
    public void generatePDF(@PathVariable("orderId") Long orderId, Model model, HttpServletResponse response) throws IOException {
        Optional<PastOrder> order = pastOrderRepo.findById(orderId);
        if (order.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Order not found");
            return;
        }

        response.setContentType("application/pdf");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd:hh:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=Dogovor_past_" + order.get().getId() + ".pdf";
        response.setHeader(headerKey, headerValue);

        pdfGeneratorService.export_past(response, order.orElse(null));
    }
}
