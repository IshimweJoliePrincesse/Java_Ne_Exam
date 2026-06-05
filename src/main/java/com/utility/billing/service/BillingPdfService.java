package com.utility.billing.service;


public interface BillingPdfService {
    byte[] generateBillPdf(Long billId);
}
