package com.example.evcharge.helper;

import com.example.evcharge.models.ChargingStation;
import com.example.evcharge.models.ElectricVehicle;
import com.example.evcharge.models.ElectricVehicleChargedValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

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
            ChargingStation cs = new ChargingStation(i + 1, i + 1 + "th Charging Station Experiment", plugsIds);
            csList.add(cs);
        }
        return csList;
    }

    private List<ElectricVehicle> generateEVs(Integer cars, String chargeType, Integer timeslots, Integer startTime) {
        List<ElectricVehicle> evList = new ArrayList<>();
        for (Integer i = 0; i < cars; i++) {
            Random ran = new Random();
            Integer currentSOC;
            if (chargeType.equals("Charge")) {
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
        int dividingCt =100;
        if(evList.size() < 51 && evList.size() >20){
            dividingCt =200;
        }
        if(evList.size() < 25){
            dividingCt =400;
        }

        ArrayList<Double> ediffList = dataGathering.collectEdiffEXP(time, startTime, chargeType,dividingCt);
        return whaleSolutionParser.parseSolution(eVhelper.whaleOptimizationAlgorithm(100, charginSt * 2, time, csList, evList, ediffList, 150, chargeType), startTime);
    }

    public int getCsnum(MultipartFile file) throws IOException {
        String csvContent = new String(file.getBytes(), StandardCharsets.UTF_8);
        String[] lines = csvContent.split("\n");
        String[] columns = lines[0].split("[,\t;]");

        // Find the column index for "Csnum number"
        int csnumIndex = -1;
        for (int i = 0; i < columns.length; i++) {
            if (columns[i].trim().equalsIgnoreCase("Csnum number")) {
                csnumIndex = i;
                break;
            }
        }

        if (csnumIndex != -1) {
            String[] values = lines[1].split("[,\t;]");
            if (csnumIndex < values.length) {
                return Integer.parseInt(values[csnumIndex].trim());
            }
        }

        // Return a default value or throw an exception if the column is not found
        throw new IllegalArgumentException("Column 'Csnum number' not found in the CSV file.");
    }

    public int getWindowNumber(MultipartFile file) throws IOException {
        String csvContent = new String(file.getBytes(), StandardCharsets.UTF_8);
        String[] lines = csvContent.split("\n");
        String[] columns = lines[0].split("[,\t;]");

        // Find the column index for "Window number"
        int windowNumberIndex = -1;
        for (int i = 0; i < columns.length; i++) {
            if (columns[i].trim().equalsIgnoreCase("Window number")) {
                windowNumberIndex = i;
                break;
            }
        }

        if (windowNumberIndex != -1) {
            String[] values = lines[1].split("[,\t;]");
            if (windowNumberIndex < values.length) {
                return Integer.parseInt(values[windowNumberIndex].trim());
            }
        }

        // Return a default value or throw an exception if the column is not found
        throw new IllegalArgumentException("Column 'Window number' not found in the CSV file.");
    }

    public List<ElectricVehicle> getElectricVehicles(MultipartFile file) throws IOException {
        String csvContent = new String(file.getBytes(), StandardCharsets.UTF_8);
        String[] lines = csvContent.split("\n");

        // Find the column indices for each attribute
        int idIndex = -1;
        int maxCapacityIndex = -1;
        int minSOCIndex = -1;
        int chrDisPerHourIndex = -1;
        int favouriteChargingStationIndex = -1;
        int favouriteTimeSlotsIndex = -1;
        int constraintsPenaltyIndex = -1;
        int socCurrentIndex = -1;

        // Find the header line index
        int headerIndex = -1;
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].startsWith("id,maxCapacity")) {
                headerIndex = i;
                break;
            }
        }

        if (headerIndex != -1) {
            String[] columns = lines[headerIndex].split(",");

            // Find the column indices
            for (int i = 0; i < columns.length; i++) {
                switch (columns[i]) {
                    case "id":
                        idIndex = i;
                        break;
                    case "maxCapacity":
                        maxCapacityIndex = i;
                        break;
                    case "minSOC":
                        minSOCIndex = i;
                        break;
                    case "chrDisPerHour":
                        chrDisPerHourIndex = i;
                        break;
                    case "favouriteChargingStation":
                        favouriteChargingStationIndex = i;
                        break;
                    case "favouriteTimeSlots":
                        favouriteTimeSlotsIndex = i;
                        break;
                    case "constraintsPenalty":
                        constraintsPenaltyIndex = i;
                        break;
                    case "soccurrent":
                        socCurrentIndex = i;
                        break;
                }
            }

            // Process the data lines
            List<ElectricVehicle> electricVehicles = new ArrayList<>();
            for (int i = headerIndex + 1; i < lines.length; i++) {
                String[] values = lines[i].split(",");
                if (values.length > 0) {
                    ElectricVehicle electricVehicle = new ElectricVehicle();
                    electricVehicle.setId(Integer.parseInt(values[idIndex].trim()));
                    electricVehicle.setMaxCapacity(Integer.parseInt(values[maxCapacityIndex].trim()));
                    electricVehicle.setMinSOC(Integer.parseInt(values[minSOCIndex].trim()));
                    electricVehicle.setChrDisPerHour(Integer.parseInt(values[chrDisPerHourIndex].trim()));
                    electricVehicle.setFavouriteChargingStation(
                            new ChargingStation(Integer.parseInt(values[favouriteChargingStationIndex].trim()))
                    );
                    electricVehicle.setFavouriteTimeSlots(Arrays.stream(values[favouriteTimeSlotsIndex].trim().split(";"))
                            .map(Integer::parseInt)
                            .collect(Collectors.toList()));
                    electricVehicle.setConstraintsPenalty(Arrays.stream(values[constraintsPenaltyIndex].trim().split(";"))
                            .map(Integer::parseInt)
                            .collect(Collectors.toList()));
                    electricVehicle.setSOCcurrent(Integer.parseInt(values[socCurrentIndex].trim()));
                    electricVehicles.add(electricVehicle);
                }
            }

            return electricVehicles;
        }

        throw new IllegalArgumentException("Header line not found in the CSV file.");
    }



    public List<List<Object>> getExperimentWithCSV(Integer startTime, String chargeType, MultipartFile file) throws IOException {
        Integer charginSt = this.getCsnum(file); //change from csv
        Integer time = this.getWindowNumber(file);

        List<ElectricVehicle> evList = this.getElectricVehicles(file);//change from csv
        List<ChargingStation> csList = this.generateCharginStations(charginSt);
        Collections.shuffle(evList);

        for (ElectricVehicle ev : evList) {
            Random rn = new Random();
            Optional<ChargingStation> optionalChargingStation = csList.stream()
                    .filter(station -> Objects.equals(station.getId(), ev.getFavouriteChargingStation().getId()))
                    .findFirst();
            ChargingStation chargingStation = new ChargingStation();
            if(optionalChargingStation.isPresent()){
                chargingStation = optionalChargingStation.get();
            }
            ev.setFavouriteChargingStation(chargingStation);
            List<ElectricVehicle> oldEvListOfCs = new ArrayList<>();
            if (chargingStation.getElectricVehicles() != null) {
                oldEvListOfCs = chargingStation.getElectricVehicles();
            }
            oldEvListOfCs.add(ev);
            chargingStation.setElectricVehicles(oldEvListOfCs);
        }
        int dividingCt =100;
        if(evList.size() < 51 && evList.size() >25){
            dividingCt =200;
        }
        if(evList.size() < 25){
            dividingCt =400;
        }
        ArrayList<Double> ediffList = dataGathering.collectEdiffEXP(time, startTime, chargeType,dividingCt);
        return whaleSolutionParser.parseSolution(eVhelper.whaleOptimizationAlgorithm(100, charginSt * 2, time, csList, evList, ediffList, 150, chargeType), startTime);
    }

    public String generateCsvContent(List<ElectricVehicleChargedValue> solution, Integer csnum, Integer window) {
        StringBuilder csvContent = new StringBuilder();

        // Append column names
        csvContent.append("Csnum number,");
        csvContent.append("Window number\n");

        // Append parameter values
        csvContent.append(csnum).append(",");
        csvContent.append(window).append("\n");

        // Append object details
        csvContent.append("id,maxCapacity,minSOC,chrDisPerHour,favouriteChargingStation,favouriteTimeSlots,constraintsPenalty,soccurrent\n");
        for (ElectricVehicleChargedValue obj : solution) {
            csvContent.append(obj.getElectricVehicle().getId()).append(",");
            csvContent.append(obj.getElectricVehicle().getMaxCapacity()).append(",");
            csvContent.append(obj.getElectricVehicle().getMinSOC()).append(",");
            csvContent.append(obj.getElectricVehicle().getChrDisPerHour()).append(",");
            csvContent.append(obj.getFavouriteChargingStation().getId()).append(",");
            csvContent.append(formatList(obj.getElectricVehicle().getFavouriteTimeSlots(), ";")).append(",");
            csvContent.append(formatList(obj.getElectricVehicle().getConstraintsPenalty(), ";")).append(",");
            csvContent.append(obj.getElectricVehicle().getSOCcurrent()).append("\n");
        }

        return csvContent.toString();
    }

    private String formatList(List<Integer> list, String delimiter) {
        StringBuilder formattedList = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            formattedList.append(list.get(i));
            if (i < list.size() - 1) {
                formattedList.append(delimiter);
            }
        }
        return formattedList.toString();
    }
}
