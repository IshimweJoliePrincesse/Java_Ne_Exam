package com.utility.billing.entity;

import com.utility.billing.enums.MeterType;
import com.utility.billing.enums.TariffType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tariffs")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Tariff {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MeterType meterType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TariffType tariffType;

    @Column(nullable = false)
    private Double pricePerUnit;

    @Column(nullable = false)
    private Double fixedCharge;

    @Column(nullable = false)
    private Double vatPercent;

    @Column(nullable = false)
    private Double latePenaltyPercent;

    @Column(nullable = false)
    private LocalDate effectiveDate;

    @Column(nullable = false)
    private Boolean isActive;

    @Column(nullable = false)
    private Integer version;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "tariff", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TariffTier> tiers = new ArrayList<>();

    @PrePersist void onCreate() { createdAt = LocalDateTime.now(); if (isActive == null) isActive = true; if (version == null) version = 1; }
}
