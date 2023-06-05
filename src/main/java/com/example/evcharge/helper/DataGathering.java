package com.example.evcharge.helper;


import com.example.evcharge.models.ElectricVehicle;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.lang.reflect.Array;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
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
                cal.add(Calendar.DATE, -1);
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String url = "";
        if(chargeType.equals("charge")){
            url="https://www.caiso.com/outlook/SP/History/"+dateFormat.format(cal.getTime())+"/fuelsource.csv";
        }else{
            url="https://www.caiso.com/outlook/SP/History/"+dateFormat.format(cal.getTime())+"/demand.csv";
        }
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
                        Double value = Double.parseDouble(line.split(",")[1])/170;
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

}
