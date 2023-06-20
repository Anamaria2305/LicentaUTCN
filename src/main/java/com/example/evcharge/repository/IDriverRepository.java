package com.example.evcharge.repository;

import com.example.evcharge.models.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IDriverRepository extends JpaRepository<Driver,Integer> {
}
