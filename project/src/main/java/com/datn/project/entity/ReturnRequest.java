package com.datn.project.entity;

import lombok.*;
import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "return_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReturnRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String reason;

    @Column(columnDefinition = "TEXT")
    private String images;

    @Enumerated(EnumType.STRING)
    private ReturnStatus status;

    @Column(columnDefinition = "TEXT")
    private String adminNote;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column(name = "ghn_return_code")
    private String ghnReturnCode;
}