package com.example.evcharge.controller;

import com.example.evcharge.helper.DriverService;
import com.example.evcharge.helper.ElectricVehicleService;
import com.example.evcharge.models.Driver;
import com.example.evcharge.models.ElectricVehicle;
import com.example.evcharge.models.ElectricVehicleChargedValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/crud")
public class CRUDController {

    @Autowired
    DriverService driverService;

    @Autowired
    ElectricVehicleService electricVehicleService;

    @RequestMapping(method = RequestMethod.POST, value = "/saveDV")
    @ResponseBody
    public void saveDV(@RequestBody Driver driver) {
        driverService.saveDriver(driver);
    }
    @RequestMapping(method = RequestMethod.POST, value = "/saveEV")
    @ResponseBody
    public void saveEV(@RequestBody ElectricVehicle electricVehicle,@RequestParam(name = "csid") Integer csid, @RequestParam(name = "driverid") Integer driverid) {
        System.out.println(electricVehicle);
        electricVehicleService.saveEV(electricVehicle,csid,driverid);
    }
}
