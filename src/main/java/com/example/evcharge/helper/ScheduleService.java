package com.example.evcharge.helper;

import com.example.evcharge.models.ChargingStation;
import com.example.evcharge.models.Driver;
import com.example.evcharge.models.ElectricVehicle;
import com.example.evcharge.models.ElectricVehicleChargedValue;
import com.example.evcharge.repository.IScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

@Service
public class ScheduleService {

    @Autowired
    IScheduleRepository iScheduleRepository;

    @Autowired
    ElectricVehicleService electricVehicleService;

    @Autowired
    DriverService driverService;

    public ElectricVehicleChargedValue saveSchedule(Integer carid, Integer valueCharged, Integer time, ChargingStation cs) {
        Optional<ElectricVehicle> ev = electricVehicleService.findById(carid);
        ElectricVehicleChargedValue schedule = null;
        if (ev.isPresent()) {
            schedule = new ElectricVehicleChargedValue(valueCharged,(time+8));
            iScheduleRepository.save(schedule);
            ev.get().setSchedule(schedule);
            schedule.setElectricVehicle(ev.get());
            schedule.setFavouriteChargingStation(cs);

            List<ElectricVehicleChargedValue> newArray = new ArrayList<>();
            if(cs.getElcv()!=null){
                newArray.addAll(cs.getElcv());
            }
            newArray.add(schedule);
            cs.setElcv(newArray);
        }
        return iScheduleRepository.save(schedule);
    }

    public List<ElectricVehicleChargedValue> saveAll(List<ElectricVehicleChargedValue> solution) {
        iScheduleRepository.deleteAllRecords();
        for (ElectricVehicleChargedValue sol :
                solution) {
            this.saveSchedule(sol.getElectricVehicle().getId(),sol.getValueCharged(),sol.getTime(),sol.getFavouriteChargingStation());
        }
        return solution;
    }

    public List<ElectricVehicleChargedValue> getAll() {
        return (List<ElectricVehicleChargedValue>) iScheduleRepository.findAll();
    }

    public List<Object> getNotification(String username){
        Optional<Driver> driver = driverService.findByUsername(username);
        if(driver.isPresent()){
            List<Object> notification = new ArrayList<>();
            Optional<ElectricVehicleChargedValue> electricVehicleCharged = this.getAll().stream().filter(evcv ->driver.get().getElectricVehicle()!=null && evcv.getElectricVehicle().getId()==driver.get().getElectricVehicle().getId()).findFirst();
            if(electricVehicleCharged.isPresent()){
                notification.add(electricVehicleCharged.get().getValueCharged());
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, +1);
                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                notification.add(dateFormat.format(cal.getTime()));
                notification.add(electricVehicleCharged.get().getTime());
                notification.add(electricVehicleCharged.get().getFavouriteChargingStation());
                return  notification;
            }
        }
        return null;
    }
}
