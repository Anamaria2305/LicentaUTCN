package com.example.evcharge.models;

import lombok.*;

import java.util.ArrayList;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ElectricVehicle {

    private Integer id;

    private Integer maxCapacity;

    private Integer SOCcurrent;

    private Integer minSOC;

    private Integer chrDisPerHour;

    private ChargingStation favouriteChargingStation;

    private ArrayList<Integer> favouriteTimeSlots;

    private ArrayList<Integer> constraintsPenalty;
}
