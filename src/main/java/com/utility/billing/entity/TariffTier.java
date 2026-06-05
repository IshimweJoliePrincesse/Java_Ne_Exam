package com.utility.billing.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "tariff_tiers")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TariffTier {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tariff_id", nullable = false)
    private Tariff tariff;

    @Column(nullable = false)
    private Double minUnits;

    private Double maxUnits;

    @Column(nullable = false)
    private Double pricePerUnit;
}
