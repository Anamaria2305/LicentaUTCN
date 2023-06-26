package com.example.evcharge.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "stations")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "electricVehicles","elcv"})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ChargingStation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String name;

    @OneToMany(mappedBy = "favouriteChargingStation",fetch = FetchType.EAGER,
            orphanRemoval = true)
    private List<ElectricVehicle> electricVehicles;

    @Convert(converter = IntegerListConverter.class)
    private List<Integer> plugIds;

    @OneToMany(mappedBy = "favouriteChargingStation",fetch = FetchType.EAGER,
            orphanRemoval = true)
    private List<ElectricVehicleChargedValue> elcv;

    public ChargingStation(Integer id, String name, List<Integer> plugIds) {
        this.id = id;
        this.name = name;
        this.plugIds = plugIds;
    }

    public ChargingStation(Integer id) {
        this.id = id;
    }
}
