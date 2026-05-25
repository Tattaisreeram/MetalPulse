package com.smarthmalik.metalpulse.core.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "trades", indexes = {
        @Index(name = "idx_trade_user_id",   columnList = "user_id"),
        @Index(name = "idx_trade_created_at", columnList = "created_at")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Trade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TradeType tradeType;

    // Populated for BUY / SELL / HOLD only
    @Column(length = 10)
    private String metal;

    @Column(length = 10)
    private String currency;

    @Column(length = 5)
    private String weightUnit;

    @Column(precision = 18, scale = 6)
    private BigDecimal quantity;

    @Column(precision = 18, scale = 4)
    private BigDecimal pricePerUnit;

    @Column(nullable = false, precision = 18, scale = 4)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false, length = 10)
    private TradeStatus status = TradeStatus.COMPLETED;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum TradeType  { BUY, SELL, HOLD, DEPOSIT, WITHDRAW, BONUS }
    public enum TradeStatus { PENDING, COMPLETED, FAILED }
}
