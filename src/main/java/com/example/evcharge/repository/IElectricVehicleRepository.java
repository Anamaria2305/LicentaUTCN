package com.example.evcharge.repository;

import com.example.evcharge.models.ElectricVehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IElectricVehicleRepository extends JpaRepository<ElectricVehicle,Integer> {
}
