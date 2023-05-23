package com.example.evcharge.helper;

import com.example.evcharge.models.ChargingStation;
import com.example.evcharge.repository.IChargingStationsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChargingStationsService {

    @Autowired
    IChargingStationsRepository iChargingStationsRepository;

    public List<ChargingStation> getAll() {
        return (List<ChargingStation>) iChargingStationsRepository.findAll();
    }
}
