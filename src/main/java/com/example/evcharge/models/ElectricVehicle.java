package com.example.evcharge.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "vehicles")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "driver"})
public class ElectricVehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private Integer maxCapacity;

    private Integer SOCcurrent;

    private Integer minSOC;

    private Integer chrDisPerHour;

    private String plateNumber;

    private String model;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "csid")
    private ChargingStation favouriteChargingStation;

    @Convert(converter = IntegerListConverter.class)
    private List<Integer> favouriteTimeSlots;

    @Convert(converter = IntegerListConverter.class)
    private List<Integer> constraintsPenalty;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "driver_id", referencedColumnName = "id")
    private Driver driver;

    public ElectricVehicle(Integer id, Integer maxCapacity, Integer SOCcurrent, Integer minSOC, Integer chrDisPerHour, List<Integer> favouriteTimeSlots, List<Integer> constraintsPenalty) {
        this.id = id;
        this.maxCapacity = maxCapacity;
        this.SOCcurrent = SOCcurrent;
        this.minSOC = minSOC;
        this.chrDisPerHour = chrDisPerHour;
        this.favouriteTimeSlots = favouriteTimeSlots;
        this.constraintsPenalty = constraintsPenalty;
    }

    public ElectricVehicle(Integer maxCapacity, Integer SOCcurrent, Integer minSOC, Integer chrDisPerHour, List<Integer> favouriteTimeSlots, List<Integer> constraintsPenalty) {
        this.maxCapacity = maxCapacity;
        this.SOCcurrent = SOCcurrent;
        this.minSOC = minSOC;
        this.chrDisPerHour = chrDisPerHour;
        this.favouriteTimeSlots = favouriteTimeSlots;
        this.constraintsPenalty = constraintsPenalty;
    }

    /*@Override
    public String toString() {
        return "ElectricVehicle{" +
                "id=" + id +
                '}';
    }*/
}
