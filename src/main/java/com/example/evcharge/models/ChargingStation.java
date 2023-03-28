package com.example.evcharge.models;

import java.util.ArrayList;
import java.util.Objects;

public class ChargingStation {

    private Integer id;

    private String name;

    private ArrayList<Integer> plugIds;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Integer> getPlugIds() {
        return plugIds;
    }

    public void setPlugIds(ArrayList<Integer> plugIds) {
        this.plugIds = plugIds;
    }

    public ChargingStation(Integer id, String name, ArrayList<Integer> plugIds) {
        this.id = id;
        this.name = name;
        this.plugIds = plugIds;
    }

    @Override
    public String toString() {
        return "ChargingStation{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", plugIds=" + plugIds +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChargingStation that = (ChargingStation) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(plugIds, that.plugIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, plugIds);
    }
}
