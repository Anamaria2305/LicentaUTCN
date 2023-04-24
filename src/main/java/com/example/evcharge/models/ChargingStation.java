package com.example.evcharge.models;

import lombok.*;

import java.util.ArrayList;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ChargingStation {

    private Integer id;

    private String name;

    private ArrayList<Integer> plugIds;

}
