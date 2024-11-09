package com.AMIR.SRM.service;

import com.AMIR.SRM.domain.Order;
import com.AMIR.SRM.domain.PastOrder;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;

@Service
public class PdfGeneratorService {
    public void export(HttpServletResponse response, Order order) throws IOException, DocumentException {
        Document document = new Document(PageSize.A4);
        PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        URL backgroundUrl = getClass().getResource("/static/files/ads_pdf.png");
        if (backgroundUrl != null) {
            Image backgroundImage = Image.getInstance(backgroundUrl);
            backgroundImage.setAbsolutePosition(0, 0);
            backgroundImage.scaleToFit(document.getPageSize().getWidth(), document.getPageSize().getHeight());
            PdfContentByte canvas = writer.getDirectContentUnder();
            canvas.addImage(backgroundImage);
        }

        BaseFont bfTNR = BaseFont.createFont("C://Windows//Fonts//Arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font fontTitle = new Font(bfTNR, 18, Font.BOLD);
        Font fontParagraph = new Font(bfTNR, 14);

        Paragraph title = new Paragraph("Договор №" + order.getId(), fontTitle);
        title.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(title);

        document.add(new Paragraph(" "));

        Paragraph[] paragraphs = new Paragraph[8];
        paragraphs[0] = new Paragraph("Номер заказа: " + order.getId(), fontParagraph);
        paragraphs[1] = new Paragraph("Исполнитель: " + order.getProvider(), fontParagraph);
        paragraphs[2] = new Paragraph("Наименование товара: " + order.getProduct_name(), fontParagraph);
        paragraphs[3] = new Paragraph("Краткое описание: " + order.getDescription(), fontParagraph);
        paragraphs[4] = new Paragraph("Дата доставки: " + order.getReal_date(), fontParagraph);
        paragraphs[5] = new Paragraph("Цена одной единицы товара: " + String.format("%.2f", order.getReal_price()) + " BYN", fontParagraph);
        paragraphs[6] = new Paragraph("Количество: " + order.getCount(), fontParagraph);
        paragraphs[7] = new Paragraph("Итого к оплате: " + String.format("%.2f", order.getReal_price() * order.getCount()) + " BYN", fontParagraph);

        for (Paragraph paragraph : paragraphs) {
            paragraph.setAlignment(Paragraph.ALIGN_LEFT);
            document.add(paragraph);
        }

        document.close();
    }

    public void export_past(HttpServletResponse response, PastOrder pastOrder) throws IOException, DocumentException {
        Document document = new Document(PageSize.A4);
        PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        URL backgroundUrl = getClass().getResource("/static/files/ads_pdf.png");
        if (backgroundUrl != null) {
            Image backgroundImage = Image.getInstance(backgroundUrl);
            backgroundImage.setAbsolutePosition(0, 0);
            backgroundImage.scaleToFit(document.getPageSize().getWidth(), document.getPageSize().getHeight());
            PdfContentByte canvas = writer.getDirectContentUnder();
            canvas.addImage(backgroundImage);
        }

        BaseFont bfTNR = BaseFont.createFont("C://Windows//Fonts//Arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font fontTitle = new Font(bfTNR, 18, Font.BOLD);
        Font fontParagraph = new Font(bfTNR, 14);

        Paragraph title = new Paragraph("Завершенный заказ\nДоговор №" + pastOrder.getId(), fontTitle);
        title.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(title);

        document.add(new Paragraph(" "));

        Paragraph[] paragraphs = new Paragraph[8];
        paragraphs[0] = new Paragraph("Номер заказа: " + pastOrder.getId(), fontParagraph);
        paragraphs[1] = new Paragraph("Исполнитель: " + pastOrder.getProvider(), fontParagraph);
        paragraphs[2] = new Paragraph("Наименование товара: " + pastOrder.getProduct_name(), fontParagraph);
        paragraphs[3] = new Paragraph("Краткое описание: " + pastOrder.getDescription(), fontParagraph);
        paragraphs[4] = new Paragraph("Дата доставки: " + pastOrder.getReal_date(), fontParagraph);
        paragraphs[5] = new Paragraph("Цена одной единицы товара: " + String.format("%.2f", pastOrder.getReal_price()) + " BYN", fontParagraph);
        paragraphs[6] = new Paragraph("Количество: " + pastOrder.getCount(), fontParagraph);
        paragraphs[7] = new Paragraph("Итого к оплате: " + String.format("%.2f", pastOrder.getReal_price() * pastOrder.getCount()) + " BYN", fontParagraph);

        for (Paragraph paragraph : paragraphs) {
            paragraph.setAlignment(Paragraph.ALIGN_LEFT);
            document.add(paragraph);
        }

        document.close();
    }
}
