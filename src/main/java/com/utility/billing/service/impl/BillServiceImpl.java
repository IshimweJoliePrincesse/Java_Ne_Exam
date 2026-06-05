package com.utility.billing.service.impl;

import com.utility.billing.dto.BillDtos.BillResponse;
import com.utility.billing.dto.BillDtos.BillSummaryResponse;
import com.utility.billing.entity.*;
import com.utility.billing.enums.BillStatus;
import com.utility.billing.enums.Status;
import com.utility.billing.enums.TariffType;
import com.utility.billing.exception.DuplicateResourceException;
import com.utility.billing.exception.InvalidOperationException;
import com.utility.billing.exception.ResourceNotFoundException;
import com.utility.billing.mapper.UtilityMappers;
import com.utility.billing.repository.BillRepository;
import com.utility.billing.repository.MeterReadingRepository;
import com.utility.billing.repository.TariffRepository;
import com.utility.billing.service.BillService;
import com.utility.billing.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Transactional
public class BillServiceImpl implements BillService {
    private final BillRepository billRepository;
    private final MeterReadingRepository readingRepository;
    private final TariffRepository tariffRepository;
    private final UtilityMappers mapper;

    @Override
    public BillResponse generate(UUID meterReadingId) {
        if (billRepository.existsByMeterReadingId(meterReadingId)) {
            throw new DuplicateResourceException("A bill already exists for this meter reading");
        }
        MeterReading reading = readingRepository.findById(meterReadingId).orElseThrow(() -> new ResourceNotFoundException("Meter reading not found"));
        Customer customer = reading.getMeter().getCustomer();
        if (customer.getStatus() != Status.ACTIVE) {
            throw new InvalidOperationException("Inactive customers cannot receive bills");
        }
        Tariff tariff = tariffRepository.findFirstByMeterTypeAndIsActiveTrueAndEffectiveDateLessThanEqualOrderByVersionDesc(
                reading.getMeter().getMeterType(), reading.getReadingDate()).orElseThrow(() -> new ResourceNotFoundException("No active tariff found"));
        double unitCharge = calculateUnitCharge(tariff, reading.getConsumption());
        double fixedCharge = tariff.getFixedCharge();
        double vatAmount = (unitCharge + fixedCharge) * tariff.getVatPercent() / 100;
        double total = unitCharge + fixedCharge + vatAmount;
        Bill bill = Bill.builder()
                .billReference(reference(reading.getYear(), reading.getMonth()))
                .customer(customer)
                .meter(reading.getMeter())
                .meterReading(reading)
                .tariff(tariff)
                .consumption(reading.getConsumption())
                .unitCharge(unitCharge)
                .fixedCharge(fixedCharge)
                .vatAmount(vatAmount)
                .penaltyAmount(0.0)
                .totalAmount(total)
                .amountPaid(0.0)
                .outstandingBalance(total)
                .billingMonth(reading.getMonth())
                .billingYear(reading.getYear())
                .status(BillStatus.PENDING)
                .dueDate(reading.getReadingDate().plusDays(30))
                .build();
        return mapper.toBillResponse(billRepository.save(bill));
    }

    @Override
    public BillResponse approve(UUID id) {
        Bill bill = get(id);
        bill.setStatus(BillStatus.APPROVED);
        bill.setApprovedBy(SecurityUtils.currentUser());
        return mapper.toBillResponse(bill);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BillSummaryResponse> findAll() {
        return billRepository.findAll().stream().map(mapper::toBillSummary).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BillResponse findById(UUID id) {
        return mapper.toBillResponse(get(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BillSummaryResponse> findByCustomer(UUID customerId) {
        return billRepository.findByCustomerIdOrderByCreatedAtDesc(customerId).stream().map(mapper::toBillSummary).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BillResponse findByReference(String reference) {
        return mapper.toBillResponse(billRepository.findByBillReference(reference).orElseThrow(() -> new ResourceNotFoundException("Bill not found")));
    }

    private double calculateUnitCharge(Tariff tariff, double consumption) {
        if (tariff.getTariffType() == TariffType.FLAT) {
            return consumption * tariff.getPricePerUnit();
        }
        return tariff.getTiers().stream().mapToDouble(tier -> {
            double max = tier.getMaxUnits() == null ? consumption : Math.min(consumption, tier.getMaxUnits());
            double units = Math.max(0, max - tier.getMinUnits());
            return units * tier.getPricePerUnit();
        }).sum();
    }

    private Bill get(UUID id) {
        return billRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Bill not found"));
    }

    private String reference(int year, int month) {
        return "BILL-%d%02d-%05d".formatted(year, month, ThreadLocalRandom.current().nextInt(1, 100000));
    }
}
