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
                electricVehiclesListCS.add(electricVehicle);
            }
            electricVehiclesListCS.add(electricVehicle);
            cs.get().setElectricVehicles(electricVehiclesListCS);
            chargingStationsService.saveCS(cs.get());
        }
        return iElectricVehicleRepository.save(electricVehicle);
    }
}
