package com.madgarage.api.services;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.madgarage.api.model.Order;
import com.madgarage.api.model.OrderItem;
import org.springframework.stereotype.Service;

import com.lowagie.text.Rectangle;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class InvoiceService {

    public byte[] generateInvoicePdf(Order order) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, baos);

        document.open();

        // Fonts
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, new Color(255, 51, 51));
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.BLACK);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);
        Font smallFont = FontFactory.getFont(FontFactory.HELVETICA, 8, Color.GRAY);

        // Header
        Paragraph header = new Paragraph("THE MAD GARAGE", titleFont);
        header.setAlignment(Element.ALIGN_CENTER);
        document.add(header);

        Paragraph subHeader = new Paragraph("Digital Performance Parts Receipt", headerFont);
        subHeader.setAlignment(Element.ALIGN_CENTER);
        subHeader.setSpacingAfter(20);
        document.add(subHeader);

        // Order Info Table (2 columns)
        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(100);
        infoTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

        PdfPCell leftCell = new PdfPCell();
        leftCell.setBorder(Rectangle.NO_BORDER);
        leftCell.addElement(new Paragraph("ORDER DETAILS", headerFont));
        leftCell.addElement(new Paragraph("Order ID: #" + order.getId(), normalFont));
        leftCell.addElement(new Paragraph("Date: " + order.getOrderDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")), normalFont));
        infoTable.addCell(leftCell);

        PdfPCell rightCell = new PdfPCell();
        rightCell.setBorder(Rectangle.NO_BORDER);
        rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        rightCell.addElement(new Paragraph("CUSTOMER", headerFont));
        rightCell.addElement(new Paragraph(order.getUser().getFirstName() + " " + order.getUser().getLastName(), normalFont));
        rightCell.addElement(new Paragraph(order.getUser().getEmail(), normalFont));
        infoTable.addCell(rightCell);

        document.add(infoTable);
        document.add(new Paragraph(" ", normalFont)); // Spacer

        // Items Table
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        try {
            table.setWidths(new float[]{3f, 1.5f, 1.5f, 1f, 1.5f, 1.5f});
        } catch (DocumentException e) {
            // Fallback
        }
        table.setSpacingBefore(10);

        // Table Headers
        String[] headers = {"Part Name", "Condition", "Color", "Qty", "Price", "Subtotal"};
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
            cell.setBackgroundColor(new Color(240, 240, 240));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(8);
            table.addCell(cell);
        }

        // Table Data
        for (OrderItem item : order.getItems()) {
            PdfPCell nameCell = new PdfPCell(new Phrase(item.getProduct().getPartName(), normalFont));
            nameCell.setPadding(5);
            table.addCell(nameCell);

            PdfPCell condCell = new PdfPCell(new Phrase(item.getProduct().getCondition() != null ? item.getProduct().getCondition().name() : "NEW", normalFont));
            condCell.setPadding(5);
            condCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(condCell);

            PdfPCell colorCell = new PdfPCell(new Phrase(item.getProduct().getColor() != null ? item.getProduct().getColor() : "N/A", normalFont));
            colorCell.setPadding(5);
            colorCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(colorCell);

            PdfPCell qtyCell = new PdfPCell(new Phrase(String.valueOf(item.getQuantity()), normalFont));
            qtyCell.setPadding(5);
            qtyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(qtyCell);

            PdfPCell priceCell = new PdfPCell(new Phrase("INR " + item.getPriceAtPurchase(), normalFont));
            priceCell.setPadding(5);
            priceCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(priceCell);

            PdfPCell subCell = new PdfPCell(new Phrase("INR " + (item.getQuantity() * item.getPriceAtPurchase()), normalFont));
            subCell.setPadding(5);
            subCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(subCell);
        }

        document.add(table);

        // Total Section
        PdfPTable totalTable = new PdfPTable(2);
        totalTable.setWidthPercentage(100);
        totalTable.setSpacingBefore(20);
        
        PdfPCell spacer = new PdfPCell();
        spacer.setBorder(Rectangle.NO_BORDER);
        totalTable.addCell(spacer);

        double subtotal = order.getSubtotal() != null ? order.getSubtotal() : 0.0;
        double tax = order.getTaxAmount() != null ? order.getTaxAmount() : 0.0;
        double shipping = order.getShippingFee() != null ? order.getShippingFee() : 0.0;
        double platform = order.getPlatformFee() != null ? order.getPlatformFee() : 0.0;
        double grandTotal = order.getGrandTotal() != null ? order.getGrandTotal() : 0.0;

        PdfPCell summaryCell = new PdfPCell();
        summaryCell.setBorder(Rectangle.NO_BORDER);
        summaryCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        
        Paragraph pSub = new Paragraph("Subtotal: INR " + String.format("%.2f", subtotal), normalFont);
        pSub.setAlignment(Element.ALIGN_RIGHT);
        summaryCell.addElement(pSub);

        if (shipping > 0) {
            Paragraph pShip = new Paragraph("Shipping: INR " + String.format("%.2f", shipping), normalFont);
            pShip.setAlignment(Element.ALIGN_RIGHT);
            summaryCell.addElement(pShip);
        }

        if (platform > 0) {
            Paragraph pPlat = new Paragraph("Platform Fee: INR " + String.format("%.2f", platform), normalFont);
            pPlat.setAlignment(Element.ALIGN_RIGHT);
            summaryCell.addElement(pPlat);
        }
        
        Paragraph pGrand = new Paragraph("Grand Total: INR " + String.format("%.2f", grandTotal), headerFont);
        pGrand.setAlignment(Element.ALIGN_RIGHT);
        pGrand.setSpacingBefore(5);
        summaryCell.addElement(pGrand);
        
        totalTable.addCell(summaryCell);
        document.add(totalTable);

        // Footer
        Paragraph footer = new Paragraph("\n\nThank you for choosing THE MAD GARAGE - Performance & Hi End Parts.", smallFont);
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);

        document.close();
        return baos.toByteArray();
    }
}
