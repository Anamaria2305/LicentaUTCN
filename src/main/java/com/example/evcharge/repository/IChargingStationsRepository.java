package com.example.evcharge.repository;

import com.example.evcharge.models.ChargingStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IChargingStationsRepository extends JpaRepository<ChargingStation,Integer> {
}
