package com.madgarage.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;
import lombok.ToString;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
@Table(name = "vehicles")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String make;

    @Column(length = 100)
    private String model;

    @Column(name = "manufacture_year", nullable = false)
    private int year;

    @Column(name = "engine_type", length = 100)
    private String engineType;

    @Column(name = "fuel_type", length = 50)
    private String fuelType;

    @Column(length = 100)
    private String generation;

    @Column(length = 100)
    private String trim;

    // Links this specific year/engine to a general Car Model (like "Creta")
    @ManyToOne
    @JoinColumn(name = "model_id", nullable = true)
    @JsonIgnore
    @ToString.Exclude
    private CarModel carModel;

    // I left your custom constructor here just in case you are calling it
    // somewhere else in your code (like `new Vehicle("Hyundai", "Creta", 2024, "Petrol")`).
    public Vehicle(String make, String model, int year, String fuelType) {
        this.make = make;
        this.model = model;
        this.year = year;
        this.fuelType = fuelType;
    }
}