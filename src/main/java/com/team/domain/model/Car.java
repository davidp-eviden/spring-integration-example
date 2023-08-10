package com.team.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "car")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Car {
    @Id
    @Column(name = "car_license_plate")
    private String licensePlate;

    @Column(name = "car_name")
    private String name;

    @Column(columnDefinition = "Decimal(5,2)", name = "car_price")
    private Double price;

    @Column(columnDefinition = "tinyint")
    private Boolean available;
}
