package com.utility.billing.service;

import java.util.UUID;

public interface BillingPdfService {
    byte[] generateBillPdf(UUID billId);
}
