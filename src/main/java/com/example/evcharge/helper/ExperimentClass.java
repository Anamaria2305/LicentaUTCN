package com.example.evcharge.helper;

import com.example.evcharge.models.ChargingStation;
import com.example.evcharge.models.ElectricVehicle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class ExperimentClass {

    @Autowired
    WhaleSolutionParser whaleSolutionParser;

    @Autowired
    DataGathering dataGathering;

    private EVhelper eVhelper = new EVhelper();

    final List<Integer> batteryCapacity = Arrays.asList(22, 24, 41, 61);
    final List<Integer> chargePerHour = Arrays.asList(18, 20, 22, 28);

    final List<List<Integer>> constraintsPenalties = Arrays.asList(Arrays.asList(3, 5), Arrays.asList(5, 3));

    final Integer minSoc = 20;

    private List<ChargingStation> generateCharginStations(Integer number) {
        List<ChargingStation> csList = new ArrayList<>();
        for (Integer i = 0; i < number; i++) {
            List<Integer> plugsIds = Arrays.asList(2 * i, 2 * i + 1);
            ChargingStation cs = new ChargingStation(i+1, i+1 + "th Charging Station Experiment", plugsIds);
            csList.add(cs);
        }
        return csList;
    }

    private List<ElectricVehicle> generateEVs(Integer cars, String chargeType, Integer timeslots, Integer startTime) {
        List<ElectricVehicle> evList = new ArrayList<>();
        for (Integer i = 0; i < cars; i++) {
            Random ran = new Random();
            Integer currentSOC;
            if (chargeType.equals("charge")) {
                currentSOC = ran.nextInt(35 - 10 + 1) + 10;
            } else {
                currentSOC = ran.nextInt(100 - 75 + 1) + 75;
            }
            Integer indexForArrays = ran.nextInt(batteryCapacity.size());
            Integer batteryCapacity = this.batteryCapacity.get(indexForArrays);
            Integer chargePerHour = this.chargePerHour.get(indexForArrays);
            List<Integer> constraint = this.constraintsPenalties.get(ran.nextInt(constraintsPenalties.size()));
            Integer numberOfHoursAvailable = ran.nextInt(timeslots) + 1;
            List<Integer> availability = new ArrayList<>();
            int j = 0;
            while (j < numberOfHoursAvailable) {
                Integer time = ran.nextInt(timeslots) + 1;
                if (!availability.contains(time)) {
                    availability.add(time);
                    j++;
                }
            }
            Collections.sort(availability);
            ElectricVehicle ev = new ElectricVehicle(i, batteryCapacity, currentSOC, minSoc, chargePerHour, availability, constraint);
            evList.add(ev);
        }
        return evList;
    }

    public List<List<Object>> getExperiment(Integer charginSt, Integer time, Integer cars, Integer startTime, String chargeType) throws IOException {
        List<ChargingStation> csList = this.generateCharginStations(charginSt);
        List<ElectricVehicle> evList = this.generateEVs(cars, chargeType, time, startTime);
        Collections.shuffle(evList);
        for (ElectricVehicle ev : evList) {
            Random rn = new Random();
            ChargingStation chargingStation = csList.get(rn.nextInt(csList.size()));
            ev.setFavouriteChargingStation(chargingStation);
            List<ElectricVehicle> oldEvListOfCs = new ArrayList<>();
            if (chargingStation.getElectricVehicles() != null) {
                oldEvListOfCs = chargingStation.getElectricVehicles();
            }
            oldEvListOfCs.add(ev);
            chargingStation.setElectricVehicles(oldEvListOfCs);
        }
        ArrayList<Double> ediffList = dataGathering.collectEdiff(time, startTime, chargeType);
        return whaleSolutionParser.parseSolution(eVhelper.whaleOptimizationAlgorithm(100, charginSt * 2, time, csList, evList, ediffList, 150,chargeType),startTime);
    }
}
