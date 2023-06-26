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

    public Optional<Driver> findByUsername(String username){
        return Optional.ofNullable(iDriverRepository.findByUsername(username));
    }

    public Driver saveDriver(Driver driver){
        return iDriverRepository.save(driver);
    }

    public Optional<Driver> login(Driver driver){
        Optional<Driver> driver1 = iDriverRepository.findAll().stream().filter(drivers -> drivers.getUsername().equals(driver.getUsername()))
                .findFirst();
        if(driver1.isPresent() && driver1.get().getPassword().equals(driver.getPassword()))
            return driver1;
        return Optional.empty();
    }
}
