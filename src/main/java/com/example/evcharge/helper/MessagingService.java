package com.example.evcharge.helper;

import com.example.evcharge.models.ElectricVehicle;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MessagingService {

    /*@Autowired
    private IMqttClient mqttClient;*/

    public List<ElectricVehicle> subscribe(final String topic) throws MqttException, InterruptedException {
        List<ElectricVehicle> electricVehicleList = new ArrayList<>();
        /*System.out.println("Connection established!");
        //schimba mai putin
        int i=10;
        while (i > 0) {
            i--;
            mqttClient.subscribeWithResponse(topic, (tpic, msg) -> {
                System.out.println(msg.getId() + " -> " + new String(msg.getPayload()));
            });
        }
        mqttClient.disconnect();*/
        return electricVehicleList;
    }
}
