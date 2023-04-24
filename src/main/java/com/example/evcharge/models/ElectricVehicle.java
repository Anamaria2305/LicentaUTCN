package com.example.evcharge.models;

import java.util.ArrayList;
import java.util.Objects;

public class ElectricVehicle {

    private Integer id;

    private Integer maxCapacity;

    private Integer SOCcurrent;

    private Integer minSOC;

    private Integer chrDisPerHour;

    private Location location;

    private Integer valueCharged;

    private ChargingStation favouriteChargingStation;

    private ArrayList<Integer> favouriteTimeSlots;

    public ChargingStation getFavouriteChargingStation() {
        return favouriteChargingStation;
    }

    public void setFavouriteChargingStation(ChargingStation favouriteChargingStation) {
        this.favouriteChargingStation = favouriteChargingStation;
    }

    public ArrayList<Integer> getFavouriteTimeSlots() {
        return favouriteTimeSlots;
    }

    public void setFavouriteTimeSlots(ArrayList<Integer> favouriteTimeSlots) {
        this.favouriteTimeSlots = favouriteTimeSlots;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(Integer maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public Integer getSOCcurrent() {
        return SOCcurrent;
    }

    public void setSOCcurrent(Integer SOCcurrent) {
        this.SOCcurrent = SOCcurrent;
    }

    public Integer getMinSOC() {
        return minSOC;
    }

    public void setMinSOC(Integer minSOC) {
        this.minSOC = minSOC;
    }

    public Integer getChrDisPerHour() {
        return chrDisPerHour;
    }

    public void setChrDisPerHour(Integer chrDisPerHour) {
        this.chrDisPerHour = chrDisPerHour;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Integer getValueCharged() {
        return valueCharged;
    }

    public void setValueCharged(Integer valueCharged) {
        this.valueCharged = valueCharged;
    }

    public ElectricVehicle(Integer id, Integer maxCapacity, Integer SOCcurrent, Integer minSOC, Integer chrDisPerHour, Location location, ChargingStation favouriteChargingStation, ArrayList<Integer> favouriteTimeSlots) {
        this.id = id;
        this.maxCapacity = maxCapacity;
        this.SOCcurrent = SOCcurrent;
        this.minSOC = minSOC;
        this.chrDisPerHour = chrDisPerHour;
        this.location = location;
        this.favouriteChargingStation = favouriteChargingStation;
        this.favouriteTimeSlots = favouriteTimeSlots;
    }

    public ElectricVehicle(Integer id, Integer maxCapacity, Integer SOCcurrent, Integer minSOC, Integer chrDisPerHour, Location location, Integer valueCharged, ChargingStation favouriteChargingStation, ArrayList<Integer> favouriteTimeSlots) {
        this.id = id;
        this.maxCapacity = maxCapacity;
        this.SOCcurrent = SOCcurrent;
        this.minSOC = minSOC;
        this.chrDisPerHour = chrDisPerHour;
        this.location = location;
        this.valueCharged = valueCharged;
        this.favouriteChargingStation = favouriteChargingStation;
        this.favouriteTimeSlots = favouriteTimeSlots;
    }

    @Override
    public String toString() {
        return "ElectricVehicle{" +
                "id=" + id +
                ", maxCapacity=" + maxCapacity +
                ", SOCcurrent=" + SOCcurrent +
                ", minSOC=" + minSOC +
                ", chrDisPerHour=" + chrDisPerHour +
                ", location=" + location +
                ", valueCharged=" + valueCharged +
                ", favouriteChargingStation=" + favouriteChargingStation +
                ", favouriteTimeSlots=" + favouriteTimeSlots +
                '}';
    }

    public ElectricVehicle deepCopy(){
        return new ElectricVehicle(this.getId(),this.getMaxCapacity(),
                this.getSOCcurrent(),this.getMinSOC(),this.getChrDisPerHour(),
                this.getLocation(),this.getValueCharged(),this.getFavouriteChargingStation(),
                this.getFavouriteTimeSlots());
    }
}
