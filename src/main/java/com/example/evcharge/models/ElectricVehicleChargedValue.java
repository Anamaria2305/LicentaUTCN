package com.example.evcharge.models;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@ToString
@Table(name = "schedule")
public class ElectricVehicleChargedValue {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "car_id", referencedColumnName = "id")
    private ElectricVehicle electricVehicle;

    private Integer valueCharged;

    private Integer time;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "csid")
    private ChargingStation favouriteChargingStation;

    public ElectricVehicleChargedValue(ElectricVehicle electricVehicle, Integer valueCharged) {
        this.electricVehicle = electricVehicle;
        this.valueCharged = valueCharged;
    }

    public ElectricVehicleChargedValue(Integer valueCharged, Integer time) {
        this.valueCharged = valueCharged;
        this.time = time;
    }

    public ElectricVehicleChargedValue(ElectricVehicle electricVehicle, Integer valueCharged, Integer time, ChargingStation favouriteChargingStation) {
        this.electricVehicle = electricVehicle;
        this.valueCharged = valueCharged;
        this.time = time;
        this.favouriteChargingStation = favouriteChargingStation;
    }

    public ElectricVehicleChargedValue(Integer valueCharged) {
        this.valueCharged = valueCharged;
    }

    public ElectricVehicleChargedValue deepCopy(){
        return new ElectricVehicleChargedValue(this.getElectricVehicle(),this.getValueCharged());
    }

}
