package com.example.evcharge.models;

import java.util.Objects;

public class ElectricVehicle {

    private Integer id;

    private Integer chrDisPerHour;

    private Double SOCcurrent;

    private Double minSOC;

    private Location location;

    private Double MaxCapacity;

    private Double valueCharged;

    public Double getValueCharged() {
        return valueCharged;
    }

    public void setValueCharged(Double valueCharged) {
        this.valueCharged = valueCharged;
    }

    public Double getMaxCapacity() {
        return MaxCapacity;
    }

    public void setMaxCapacity(Double maxCapacity) {
        MaxCapacity = maxCapacity;
    }

    public ElectricVehicle(Integer id, Integer chrDisPerHour, Double SOCcurrent, Double minSOC, Location location, Double maxCapacity) {
        this.id = id;
        this.chrDisPerHour = chrDisPerHour;
        this.SOCcurrent = SOCcurrent;
        this.minSOC = minSOC;
        this.location = location;
        MaxCapacity = maxCapacity;
    }

    public ElectricVehicle(Integer id, Integer chrDisPerHour, Double SOCcurrent, Double minSOC, Location location, Double maxCapacity, Double valueCharged) {
        this.id = id;
        this.chrDisPerHour = chrDisPerHour;
        this.SOCcurrent = SOCcurrent;
        this.minSOC = minSOC;
        this.location = location;
        MaxCapacity = maxCapacity;
        this.valueCharged = valueCharged;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getChrDisPerHour() {
        return chrDisPerHour;
    }

    public void setChrDisPerHour(Integer chrDisPerHour) {
        this.chrDisPerHour = chrDisPerHour;
    }

    public Double getSOCcurrent() {
        return SOCcurrent;
    }

    public void setSOCcurrent(Double SOCcurrent) {
        this.SOCcurrent = SOCcurrent;
    }

    public Double getMinSOC() {
        return minSOC;
    }

    public void setMinSOC(Double minSOC) {
        this.minSOC = minSOC;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "ElectricVehicle{" +
                "id=" + id +
                ", chrDisPerHour=" + chrDisPerHour +
                ", SOCcurrent=" + SOCcurrent +
                ", minSOC=" + minSOC +
                ", location=" + location +
                ", MaxCapacity=" + MaxCapacity +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ElectricVehicle that = (ElectricVehicle) o;
        return Objects.equals(id, that.id) && Objects.equals(chrDisPerHour, that.chrDisPerHour) && Objects.equals(SOCcurrent, that.SOCcurrent) && Objects.equals(minSOC, that.minSOC) && Objects.equals(location, that.location) && Objects.equals(MaxCapacity, that.MaxCapacity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, chrDisPerHour, SOCcurrent, minSOC, location, MaxCapacity);
    }
}
