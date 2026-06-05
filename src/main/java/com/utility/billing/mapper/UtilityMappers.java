package com.utility.billing.mapper;

import com.utility.billing.dto.BillDtos.BillResponse;
import com.utility.billing.dto.BillDtos.BillSummaryResponse;
import com.utility.billing.dto.MeterDtos.MeterResponse;
import com.utility.billing.dto.MeterReadingDtos.MeterReadingResponse;
import com.utility.billing.dto.NotificationResponse;
import com.utility.billing.dto.PaymentDtos.PaymentResponse;
import com.utility.billing.dto.TariffDtos.TariffResponse;
import com.utility.billing.dto.TariffDtos.TariffTierResponse;
import com.utility.billing.entity.*;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UtilityMappers {
    default MeterResponse toMeterResponse(Meter meter) {
        return new MeterResponse(meter.getId(), meter.getMeterNumber(), meter.getMeterType(), meter.getInstallationDate(),
                meter.getStatus(), meter.getCustomer().getId(), meter.getCustomer().getFullName());
    }

    default MeterReadingResponse toReadingResponse(MeterReading reading) {
        return new MeterReadingResponse(reading.getId(), reading.getMeter().getId(), reading.getMeter().getMeterNumber(),
                reading.getPreviousReading(), reading.getCurrentReading(), reading.getConsumption(), reading.getReadingDate(),
                reading.getMonth(), reading.getYear(), reading.getRecordedBy().getId(), reading.getRecordedBy().getFullName(),
                reading.getCreatedAt());
    }

    default TariffTierResponse toTierResponse(TariffTier tier) {
        return new TariffTierResponse(tier.getId(), tier.getMinUnits(), tier.getMaxUnits(), tier.getPricePerUnit());
    }

    default TariffResponse toTariffResponse(Tariff tariff) {
        List<TariffTierResponse> tiers = tariff.getTiers().stream().map(this::toTierResponse).toList();
        return new TariffResponse(tariff.getId(), tariff.getName(), tariff.getMeterType(), tariff.getTariffType(),
                tariff.getPricePerUnit(), tariff.getFixedCharge(), tariff.getVatPercent(), tariff.getLatePenaltyPercent(),
                tariff.getEffectiveDate(), tariff.getIsActive(), tariff.getVersion(), tariff.getCreatedAt(), tiers);
    }

    default BillResponse toBillResponse(Bill bill) {
        AppUser approver = bill.getApprovedBy();
        return new BillResponse(bill.getId(), bill.getBillReference(), bill.getCustomer().getId(), bill.getCustomer().getFullName(),
                bill.getMeter().getId(), bill.getMeter().getMeterNumber(), bill.getMeterReading().getId(), bill.getConsumption(),
                bill.getUnitCharge(), bill.getFixedCharge(), bill.getVatAmount(), bill.getPenaltyAmount(), bill.getTotalAmount(),
                bill.getAmountPaid(), bill.getOutstandingBalance(), bill.getBillingMonth(), bill.getBillingYear(), bill.getStatus(),
                bill.getDueDate(), approver == null ? null : approver.getId(), approver == null ? null : approver.getFullName(),
                bill.getCreatedAt(), bill.getUpdatedAt());
    }

    default BillSummaryResponse toBillSummary(Bill bill) {
        return new BillSummaryResponse(bill.getId(), bill.getBillReference(), bill.getCustomer().getFullName(), bill.getTotalAmount(),
                bill.getOutstandingBalance(), bill.getBillingMonth(), bill.getBillingYear(), bill.getStatus());
    }

    default PaymentResponse toPaymentResponse(Payment payment) {
        return new PaymentResponse(payment.getId(), payment.getBill().getId(), payment.getBill().getBillReference(),
                payment.getAmountPaid(), payment.getPaymentMethod(), payment.getPaymentDate(), payment.getRecordedBy().getId(),
                payment.getRecordedBy().getFullName(), payment.getTransactionReference(), payment.getCreatedAt());
    }

    default NotificationResponse toNotificationResponse(Notification notification) {
        return new NotificationResponse(notification.getId(), notification.getCustomer().getId(), notification.getBill().getId(),
                notification.getMessage(), notification.getType(), notification.getIsRead(), notification.getCreatedAt());
    }
}
