package com.example.evcharge.helper;

import com.example.evcharge.models.ChargingStation;
import com.example.evcharge.models.ElectricVehicleChargedValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class WhaleSolutionParser {

    @Autowired
    ChargingStationsService css;

    public List<List<Object>> parseSolution(List<List<Object>> listOfLists,Integer startTime) {
        List<List<Object>> listOfListsParsed = new ArrayList<>();
        //new ediff
        listOfListsParsed.add(((ArrayList<Object>) listOfLists.get(0).get(0)));
        //value in cars
        listOfListsParsed.add(((ArrayList<Object>) listOfLists.get(1).get(0)));

        ElectricVehicleChargedValue[][] solution = (ElectricVehicleChargedValue[][]) ((ArrayList<?>) listOfLists.get(2).get(0)).get(0);

        List<ElectricVehicleChargedValue> transformedSolution = new ArrayList<>();
        ArrayList<Integer> charginHours = new ArrayList<>();
        ArrayList<ChargingStation> chargingStations = new ArrayList<>();
        int row = solution.length;
        int col = solution[0].length;
        ArrayList<String> mesajeFrontEnd = new ArrayList<>();
        for (int j = 0; j < col; j++) {
            for (int i = 0; i < row; i++) {
                if (solution[i][j] != null) {
                    transformedSolution.add(solution[i][j]);
                    charginHours.add(j);
                    int id = 0;
                    if (i % 2 == 0) {
                        id = (i + 2) / 2;
                    } else {
                        id = (i + 1) / 2;
                    }
                    Optional<ChargingStation> cs = css.findById(id);
                    cs.ifPresent(chargingStations::add);
                }
            }
        }
        String m1 = "Electric Vehicles at this stations: ";
        String m2 = "Electric Vehicles at this stations: ";
        String m3 = "Electric Vehicles at this stations: ";
        String m4 = "Electric Vehicles at this stations: ";
        for (int i = 0; i < chargingStations.size(); i++) {
            Integer idCS = chargingStations.get(i).getId();
            switch (idCS) {
                case 1:
                    m1 += transformedSolution.get(i).getElectricVehicle().getPlateNumber()+ " at time: "+(charginHours.get(i)+startTime)+":00" +",";
                    break;
                case 2:
                    m2 += transformedSolution.get(i).getElectricVehicle().getPlateNumber()+ " at time: "+(charginHours.get(i)+startTime)+":00" +",";
                    break;
                case 3:
                    m3 += transformedSolution.get(i).getElectricVehicle().getPlateNumber()+ " at time: "+(charginHours.get(i)+startTime)+":00" +",";
                    break;
                default:
                    m4 += transformedSolution.get(i).getElectricVehicle().getPlateNumber()+ " at time: "+(charginHours.get(i)+startTime)+":00" +",";
                    break;
            }
        }
        mesajeFrontEnd.add(m1);
        mesajeFrontEnd.add(m2);
        mesajeFrontEnd.add(m3);
        mesajeFrontEnd.add(m4);
        //transformed solution
        listOfListsParsed.add(Collections.singletonList(transformedSolution));
        //hours
        listOfListsParsed.add(Collections.singletonList(charginHours));
        //stations
        listOfListsParsed.add(Collections.singletonList(chargingStations));
        //correlation
        listOfListsParsed.add(Collections.singletonList(listOfLists.get(3).get(0)));
        //messages
        listOfListsParsed.add(Collections.singletonList(mesajeFrontEnd));
        //fitness ev
        listOfListsParsed.add(Collections.singletonList(listOfLists.get(4).get(0)));
        //conv rate
        listOfListsParsed.add(Collections.singletonList(listOfLists.get(5).get(0)));
        //time elapsed
        listOfListsParsed.add(Collections.singletonList(listOfLists.get(6).get(0)));
        //constraint viol
        listOfListsParsed.add(Collections.singletonList(listOfLists.get(7).get(0)));
        //euclidean
        listOfListsParsed.add(Collections.singletonList(listOfLists.get(8).get(0)));
        return listOfListsParsed;
    }
}
