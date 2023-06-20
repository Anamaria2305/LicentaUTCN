package com.example.evcharge.helper;

import com.example.evcharge.models.Driver;
import com.example.evcharge.repository.IDriverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DriverService {

    @Autowired
    IDriverRepository iDriverRepository;

    public List<Driver> getAll() {
        return (List<Driver>) iDriverRepository.findAll();
    }

    public Optional<Driver> findById(Integer id){
        return iDriverRepository.findById(id);
    }

    public Driver saveDriver(Driver driver){
        return iDriverRepository.save(driver);
    }
}
