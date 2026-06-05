package com.utility.billing.entity;

import com.utility.billing.enums.BillStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "bills")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Bill {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String billReference;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "meter_id", nullable = false)
    private Meter meter;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "meter_reading_id", nullable = false, unique = true)
    private MeterReading meterReading;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tariff_id", nullable = false)
    private Tariff tariff;

    @Column(nullable = false) private Double consumption;
    @Column(nullable = false) private Double unitCharge;
    @Column(nullable = false) private Double fixedCharge;
    @Column(nullable = false) private Double vatAmount;
    @Column(nullable = false) private Double penaltyAmount;
    @Column(nullable = false) private Double totalAmount;
    @Column(nullable = false) private Double amountPaid;
    @Column(nullable = false) private Double outstandingBalance;
    @Column(nullable = false) private Integer billingMonth;
    @Column(nullable = false) private Integer billingYear;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BillStatus status;

    @Column(nullable = false)
    private LocalDate dueDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private AppUser approvedBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist void onCreate() { createdAt = LocalDateTime.now(); updatedAt = createdAt; if (amountPaid == null) amountPaid = 0.0; if (status == null) status = BillStatus.PENDING; }
    @PreUpdate void onUpdate() { updatedAt = LocalDateTime.now(); }
}
