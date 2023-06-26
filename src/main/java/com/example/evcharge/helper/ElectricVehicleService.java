package com.example.evcharge.helper;

import com.example.evcharge.models.ChargingStation;
import com.example.evcharge.models.Driver;
import com.example.evcharge.models.ElectricVehicle;
import com.example.evcharge.repository.IElectricVehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ElectricVehicleService {

    @Autowired
    IElectricVehicleRepository iElectricVehicleRepository;

    @Autowired
    ChargingStationsService chargingStationsService;

    @Autowired
    DriverService driverService;

    public List<ElectricVehicle> getAll() {
        return (List<ElectricVehicle>) iElectricVehicleRepository.findAll();
    }

    public ElectricVehicle saveEV(ElectricVehicle electricVehicle,Integer csid,Integer driverid){
        Optional<ChargingStation> cs= chargingStationsService.findById(csid);
        Optional<Driver> dv = driverService.findById(driverid);
        iElectricVehicleRepository.save(electricVehicle);
        if (cs.isPresent() && dv.isPresent()){
            electricVehicle.setDriver(dv.get());
            electricVehicle.setFavouriteChargingStation(cs.get());

            dv.get().setElectricVehicle(electricVehicle);
            driverService.saveDriver(dv.get());

            List<ElectricVehicle> electricVehiclesListCS = new ArrayList<>();
            if(cs.get().getElectricVehicles()!=null){
                electricVehiclesListCS.addAll(cs.get().getElectricVehicles());
            }
            electricVehiclesListCS.add(electricVehicle);
            cs.get().setElectricVehicles(electricVehiclesListCS);
            chargingStationsService.saveCS(cs.get());
        }
        return iElectricVehicleRepository.save(electricVehicle);
    }

    public ElectricVehicle findByUsername(String username){
        Optional<ElectricVehicle> electricVehicle = iElectricVehicleRepository.findAll().stream().filter(ev -> ev.getDriver() != null && ev.getDriver().getUsername().equals(username)).findFirst();
        return electricVehicle.isPresent() ? electricVehicle.get() : null;
    }

    public Optional<ElectricVehicle> findById(Integer id){
        return iElectricVehicleRepository.findById(id);
    }

    public ElectricVehicle editVehicle(ElectricVehicle ev, String username){
        ElectricVehicle electricVehicle = this.findByUsername(username);
        if(electricVehicle!=null){
            electricVehicle.setPlateNumber(ev.getPlateNumber());
            electricVehicle.setModel(ev.getModel());
            electricVehicle.setMaxCapacity(ev.getMaxCapacity());
            electricVehicle.setConstraintsPenalty(ev.getConstraintsPenalty());
            electricVehicle.setFavouriteTimeSlots(ev.getFavouriteTimeSlots());
            electricVehicle.setFavouriteChargingStation(ev.getFavouriteChargingStation());
        }
        return iElectricVehicleRepository.save(electricVehicle);
    }

    public ElectricVehicle saveVehicle(ElectricVehicle ev, String username){
        Optional<Driver> driver = driverService.findByUsername(username);
        if(driver.isPresent()){
            iElectricVehicleRepository.save(ev);
            driver.get().setElectricVehicle(ev);
            ev.setDriver(driver.get());
            List<ElectricVehicle> newArray = new ArrayList<>();
            if(ev.getFavouriteChargingStation().getElectricVehicles()!=null){
                newArray.addAll(ev.getFavouriteChargingStation().getElectricVehicles());
            }
            newArray.add(ev);
            ev.getFavouriteChargingStation().setElectricVehicles(newArray);
            chargingStationsService.saveCS(ev.getFavouriteChargingStation());
        }
        return iElectricVehicleRepository.save(ev);
    }
}
