package com.example.evcharge.helper;

import com.example.evcharge.models.ElectricVehicle;
import com.example.evcharge.repository.IElectricVehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ElectricVehicleService {

    @Autowired
    IElectricVehicleRepository iElectricVehicleRepository;

    public List<ElectricVehicle> getAll() {
        return (List<ElectricVehicle>) iElectricVehicleRepository.findAll();
    }
}
