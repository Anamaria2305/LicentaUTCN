package com.example.evcharge.helper;


import com.example.evcharge.models.ChargingStation;
import com.example.evcharge.models.ElectricVehicle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DataGathering{


   /* @Autowired
    private  MessagingService messagingService;*/

    @Autowired
    private ChargingStationsService chargingStationsService;

    @Autowired
    private ElectricVehicleService electricVehicleService;


    private   List<ElectricVehicle> electricVehicleList;

    public ArrayList<Double> collectEdiff(Integer plugs, Integer startTime, String chargeType){
        ArrayList<Double> ediff = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, -2);
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String url = "";
        double chargeConstant = 1.0;
        if(!chargeType.equals("Charge")){
            chargeConstant = -1;
        }
        url="https://www.caiso.com/outlook/SP/History/"+dateFormat.format(cal.getTime())+"/fuelsource.csv";
        try (BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream("Supply.csv")) {
            byte dataBuffer[] = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
            int start=6*startTime;
            int end = start + 6 *(plugs-1);

            String line;
            try (BufferedReader br = new BufferedReader(new FileReader("Supply.csv"))) {
                int index=0;
                while(br.readLine()!=null || index <=end){
                    line = br.readLine();
                    if(start<=index && end>=index && index % 6 == 0){
                        Double value = chargeConstant * Double.parseDouble(line.split(",")[1])/100;
                        ediff.add(value);
                    }
                    index++;
                }
            }
        } catch (IOException e) {
           for(int i=0;i<plugs;i++){
               Random r = new Random();
               ediff.add(r.nextDouble(130, 150));
             }
        }
        return ediff;
    }

    public int getWindow(MultipartFile file) throws IOException {
        String csvContent = new String(file.getBytes(), StandardCharsets.UTF_8);
        String[] lines = csvContent.split("\n");
        String[] columns = lines[0].split("[,\t;]");

        // Find the column index for "Csnum number"
        int csnumIndex = -1;
        for (int i = 0; i < columns.length; i++) {
            if (columns[i].trim().equalsIgnoreCase("Window number")) {
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
    public ArrayList<Double> collectEdiffCSV(MultipartFile file,Integer startTime, String chargeType) throws IOException {
        Integer plugs =  this.getWindow(file);
        int dividingCt =100;
        if(this.getElectricVehicles(file).size() < 51 && this.getElectricVehicles(file).size()>25){
            dividingCt =200;
        }
        if(this.getElectricVehicles(file).size() < 25){
            dividingCt =400;
        }
        return this.collectEdiffEXP(plugs,startTime,chargeType,dividingCt);
    }


    public  List<ElectricVehicle> collectData(Integer maxValue){
        final String topic = "charging_stations_real_time_data";
        //final String topic = "obd_real_time_data";
        /*try {
            electricVehicleList = messagingService.subscribe(topic);
        } catch (MqttException | InterruptedException e) {
            e.printStackTrace();
        }
        if(electricVehicleList.isEmpty()){

        }*/
        electricVehicleList = electricVehicleService.getAll().stream().limit(maxValue).collect(Collectors.toList());
        return electricVehicleList;
    }

    public  List<ElectricVehicle> collectDataNoLimit(){
        final String topic = "charging_stations_real_time_data";
        //final String topic = "obd_real_time_data";
        /*try {
            electricVehicleList = messagingService.subscribe(topic);
        } catch (MqttException | InterruptedException e) {
            e.printStackTrace();
        }
        if(electricVehicleList.isEmpty()){

        }*/
        electricVehicleList = electricVehicleService.getAll().stream().collect(Collectors.toList());
        return electricVehicleList;
    }

    public ArrayList<Double> collectEdiffEXP(Integer plugs, Integer startTime, String chargeType,Integer dividingConstant){
        ArrayList<Double> ediff = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String url = "";
        double chargeConstant = 1.0;
        if(!chargeType.equals("Charge")){
            chargeConstant = -1;
        }
        url="https://www.caiso.com/outlook/SP/History/"+dateFormat.format(cal.getTime())+"/fuelsource.csv";
        try (BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream("Supply.csv")) {
            byte dataBuffer[] = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
            int start=6*startTime;
            int end = start + 6 *(plugs-1);

            String line;
            try (BufferedReader br = new BufferedReader(new FileReader("Supply.csv"))) {
                int index=0;
                while(br.readLine()!=null || index <=end){
                    line = br.readLine();
                    if(start<=index && end>=index && index % 6 == 0){
                        Double value = chargeConstant * Double.parseDouble(line.split(",")[1])/dividingConstant;
                        ediff.add(value);
                    }
                    index++;
                }
            }
        } catch (IOException e) {
            for(int i=0;i<plugs;i++){
                Random r = new Random();
                ediff.add(r.nextDouble(130, 150));
            }
        }
        return ediff;
    }

}
