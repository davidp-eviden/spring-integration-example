package com.example.spring.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "contract_processed")
public class ContractProcessed {
    @Id
    private Long policyId;
    private String policy; // poliza in spanish
    private String policySituation; // EN_VIGOR,
    private String policyBrand; // FONDO, NO_CORTE, AMBAS
    private LocalDateTime policyDate;
    private boolean expired;
    private boolean disabled;

}
