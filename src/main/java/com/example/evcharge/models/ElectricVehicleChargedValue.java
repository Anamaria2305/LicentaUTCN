package com.example.evcharge.models;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class ElectricVehicleChargedValue {

    private ElectricVehicle electricVehicle;

    private Integer valueCharged;

    public ElectricVehicleChargedValue deepCopy(){
        return new ElectricVehicleChargedValue(this.getElectricVehicle(),this.getValueCharged());
    }

    @Override
    public String toString() {
        return "ECV{" +
                "ev=" + electricVehicle +
                ", val=" + valueCharged +
                '}';
    }
}
