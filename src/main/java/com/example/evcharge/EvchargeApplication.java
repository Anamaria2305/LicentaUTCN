package com.example.evcharge;


import com.example.evcharge.helper.EVhelper;
import com.example.evcharge.helper.MessagingService;
import com.example.evcharge.models.ChargingStation;
import com.example.evcharge.models.ElectricVehicle;
import com.example.evcharge.models.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@SpringBootApplication
public class EvchargeApplication implements CommandLineRunner {

    @Autowired
    private MessagingService messagingService;

    @Autowired
    private ConfigurableApplicationContext context;

    private EVhelper eVhelper= new EVhelper();
    public static void main(String[] args) {
        SpringApplication.run(EvchargeApplication.class, args);
    }

    List<ElectricVehicle> electricVehicleList;

    @Override
    public void run(String... args) throws Exception {
        final String topic = "charging_stations_real_time_data";
        //final String topic = "obd_real_time_data";
        electricVehicleList = messagingService.subscribe(topic);
        List<ChargingStation> chargingStationList = new ArrayList<>();
        chargingStationList.add(new ChargingStation(1,"First Station",new ArrayList<>(Arrays.asList(0,1))));
        chargingStationList.add(new ChargingStation(2,"Second Station",new ArrayList<>(Arrays.asList(2,3))));
        if(electricVehicleList.isEmpty()){
            electricVehicleList.add(new ElectricVehicle(1,22,27,20,10,new Location(23.4,23.6),chargingStationList.get(0),new ArrayList<>(Arrays.asList(1,2))));
            electricVehicleList.add(new ElectricVehicle(2,22,77,20,10,new Location(23.49,23.69),chargingStationList.get(1),new ArrayList<>(Arrays.asList(1,3))));
            electricVehicleList.add(new ElectricVehicle(3,22,47,20,10,new Location(23.88,23.86),chargingStationList.get(0),new ArrayList<>(Arrays.asList(2,3))));
            electricVehicleList.add(new ElectricVehicle(4,41,49,20,10,new Location(23.4789,23.346),chargingStationList.get(1),new ArrayList<>(Arrays.asList(1,2))));
            electricVehicleList.add(new ElectricVehicle(5,41,64,20,10,new Location(23.344,23.986),chargingStationList.get(0),new ArrayList<>(Arrays.asList(2,3))));
            electricVehicleList.add(new ElectricVehicle(6,41,88,20,10,new Location(23.849,23.766),chargingStationList.get(1),new ArrayList<>(Arrays.asList(1,3))));
            electricVehicleList.add(new ElectricVehicle(7,41,26,20,10,new Location(23.6894,23.346),chargingStationList.get(0),new ArrayList<>(Arrays.asList(1,2))));
            electricVehicleList.add(new ElectricVehicle(8,24,95,20,10,new Location(23.1234,23.7613),chargingStationList.get(1),new ArrayList<>(Arrays.asList(1,2))));
            electricVehicleList.add(new ElectricVehicle(9,24,24,20,10,new Location(23.354,23.9613),chargingStationList.get(0),new ArrayList<>(Arrays.asList(1,3))));
            electricVehicleList.add(new ElectricVehicle(10,41,35,20,10,new Location(23.948,23.621),chargingStationList.get(1),new ArrayList<>(Arrays.asList(2,3))));
        }

        //apel de functie
        eVhelper.whaleOptimizationAlgorithm(120,4,3,chargingStationList,electricVehicleList);
    }
}
