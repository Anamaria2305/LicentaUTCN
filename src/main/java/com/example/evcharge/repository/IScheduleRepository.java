package com.example.evcharge.repository;

import com.example.evcharge.models.ElectricVehicleChargedValue;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IScheduleRepository extends JpaRepository<ElectricVehicleChargedValue,Integer> {

    @Transactional
    @Modifying
    @Query("DELETE FROM ElectricVehicleChargedValue")
    void deleteAllRecords();
}
