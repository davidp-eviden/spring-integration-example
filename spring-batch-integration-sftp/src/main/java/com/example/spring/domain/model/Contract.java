package com.example.spring.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "contract")
public class Contract {
    @Id
    @Column(name = "policy_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long policyId;

    @Column(name = "policy_name")
    private String policy; // poliza in spanish

    @Column(name = "policy_situation")
    private String policySituation; // EN_VIGOR,

    @Column(name = "policy_brand")
    private String policyBrand; // FONDO, NO_CORTE, AMBAS

    @Column(name = "policy_date", columnDefinition = "DATETIME")
    private LocalDateTime policyDate;

    @Column(columnDefinition = "TINYINT")
    private boolean expired;

    @Column(columnDefinition = "TINYINT")
    private boolean disabled;
}
