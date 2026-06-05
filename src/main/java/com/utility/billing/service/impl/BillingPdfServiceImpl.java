package com.utility.billing.service.impl;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.utility.billing.entity.Bill;
import com.utility.billing.enums.Role;
import com.utility.billing.exception.ResourceNotFoundException;
import com.utility.billing.exception.UnauthorizedException;
import com.utility.billing.repository.BillRepository;
import com.utility.billing.service.BillingPdfService;
import com.utility.billing.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.text.NumberFormat;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class BillingPdfServiceImpl implements BillingPdfService {
    private static final Color BRAND_BLUE = new Color(16, 82, 147);
    private static final Color LIGHT_BLUE = new Color(232, 241, 250);
    private static final Color DARK_TEXT = new Color(35, 35, 35);

    private final BillRepository billRepository;

    @Override
    @Transactional(readOnly = true)
    public byte[] generateBillPdf(Long billId) {
        Bill bill = billRepository.findById(billId).orElseThrow(() -> new ResourceNotFoundException("Bill not found"));
        ensureCanDownload(bill);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 42, 42, 36, 36);
            PdfWriter.getInstance(document, out);
            document.open();

            addHeader(document, bill);
            addCustomerSection(document, bill);
            addChargesSection(document, bill);
            addPaymentSection(document, bill);
            addFooter(document);

            document.close();
            return out.toByteArray();
        } catch (Exception ex) {
            throw new IllegalStateException("Could not generate bill PDF", ex);
        }
    }

    private void ensureCanDownload(Bill bill) {
        var user = SecurityUtils.currentUser();
        if (user.getRole() == Role.ROLE_CUSTOMER && !bill.getCustomer().getEmail().equalsIgnoreCase(user.getEmail())) {
            throw new UnauthorizedException("Customers can only download their own bills");
        }
    }

    private void addHeader(Document document, Bill bill) throws DocumentException {
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, BRAND_BLUE);
        Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 10, DARK_TEXT);
        Paragraph title = new Paragraph("UTILITY BILLING SYSTEM", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        Paragraph subtitle = new Paragraph("WASAC / REG Postpaid Utility Bill", subtitleFont);
        subtitle.setAlignment(Element.ALIGN_CENTER);
        subtitle.setSpacingAfter(18);
        document.add(subtitle);

        PdfPTable summary = new PdfPTable(2);
        summary.setWidthPercentage(100);
        summary.setWidths(new float[]{1.2f, 1f});
        addInfoCell(summary, "Bill Reference", bill.getBillReference(), true);
        addInfoCell(summary, "Status", bill.getStatus().name(), true);
        addInfoCell(summary, "Billing Period", "%02d/%d".formatted(bill.getBillingMonth(), bill.getBillingYear()), false);
        addInfoCell(summary, "Due Date", bill.getDueDate().toString(), false);
        document.add(summary);
        document.add(Chunk.NEWLINE);
    }

    private void addCustomerSection(Document document, Bill bill) throws DocumentException {
        addSectionTitle(document, "Customer and Meter Details");
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        addInfoCell(table, "Customer", bill.getCustomer().getFullName(), false);
        addInfoCell(table, "National ID", bill.getCustomer().getNationalId(), false);
        addInfoCell(table, "Phone", bill.getCustomer().getPhoneNumber(), false);
        addInfoCell(table, "Address", bill.getCustomer().getAddress(), false);
        addInfoCell(table, "Meter Number", bill.getMeter().getMeterNumber(), false);
        addInfoCell(table, "Meter Type", bill.getMeter().getMeterType().name(), false);
        document.add(table);
        document.add(Chunk.NEWLINE);
    }

    private void addChargesSection(Document document, Bill bill) throws DocumentException {
        addSectionTitle(document, "Billing Breakdown");
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{2f, 1f});
        addHeaderCell(table, "Description");
        addHeaderCell(table, "Amount");
        addAmountRow(table, "Consumption (" + bill.getConsumption() + " units)", bill.getUnitCharge());
        addAmountRow(table, "Fixed service charge", bill.getFixedCharge());
        addAmountRow(table, "VAT", bill.getVatAmount());
        addAmountRow(table, "Penalty", bill.getPenaltyAmount());
        addAmountRow(table, "Total amount", bill.getTotalAmount());
        document.add(table);
        document.add(Chunk.NEWLINE);
    }

    private void addPaymentSection(Document document, Bill bill) throws DocumentException {
        addSectionTitle(document, "Payment Summary");
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        addAmountRow(table, "Amount paid", bill.getAmountPaid());
        addAmountRow(table, "Outstanding balance", bill.getOutstandingBalance());
        document.add(table);
    }

    private void addFooter(Document document) throws DocumentException {
        Paragraph footer = new Paragraph("\nThank you for using WASAC/REG utility services. Please pay before the due date to avoid penalties.",
                FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9, DARK_TEXT));
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);
    }

    private void addSectionTitle(Document document, String text) throws DocumentException {
        Paragraph title = new Paragraph(text, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, BRAND_BLUE));
        title.setSpacingBefore(8);
        title.setSpacingAfter(6);
        document.add(title);
    }

    private void addInfoCell(PdfPTable table, String label, String value, boolean highlighted) {
        PdfPCell cell = new PdfPCell(new Phrase(label + "\n" + value, FontFactory.getFont(FontFactory.HELVETICA, 10, DARK_TEXT)));
        cell.setPadding(10);
        cell.setBackgroundColor(highlighted ? LIGHT_BLUE : Color.WHITE);
        cell.setBorderColor(new Color(215, 225, 235));
        table.addCell(cell);
    }

    private void addHeaderCell(PdfPTable table, String value) {
        PdfPCell cell = new PdfPCell(new Phrase(value, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE)));
        cell.setBackgroundColor(BRAND_BLUE);
        cell.setPadding(8);
        table.addCell(cell);
    }

    private void addAmountRow(PdfPTable table, String label, Double amount) {
        table.addCell(bodyCell(label));
        table.addCell(bodyCell(NumberFormat.getNumberInstance(Locale.US).format(amount) + " FRW"));
    }

    private PdfPCell bodyCell(String value) {
        PdfPCell cell = new PdfPCell(new Phrase(value, FontFactory.getFont(FontFactory.HELVETICA, 10, DARK_TEXT)));
        cell.setPadding(8);
        cell.setBorderColor(new Color(225, 225, 225));
        return cell;
    }
}
