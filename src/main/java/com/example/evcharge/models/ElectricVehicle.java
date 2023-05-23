package com.example.evcharge.models;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "vehicles")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ElectricVehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private Integer maxCapacity;

    private Integer SOCcurrent;

    private Integer minSOC;

    private Integer chrDisPerHour;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "csid")
    private ChargingStation favouriteChargingStation;

    @Convert(converter = IntegerListConverter.class)
    private List<Integer> favouriteTimeSlots;

    @Convert(converter = IntegerListConverter.class)
    private List<Integer> constraintsPenalty;

    @Override
    public String toString() {
        return "ElectricVehicle{" +
                "id=" + id +
                '}';
    }
}
