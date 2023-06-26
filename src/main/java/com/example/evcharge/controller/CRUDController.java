package com.example.evcharge.controller;

import com.example.evcharge.helper.DriverService;
import com.example.evcharge.helper.ElectricVehicleService;
import com.example.evcharge.helper.ScheduleService;
import com.example.evcharge.models.Driver;
import com.example.evcharge.models.ElectricVehicle;
import com.example.evcharge.models.ElectricVehicleChargedValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/crud")
public class CRUDController {

    @Autowired
    DriverService driverService;

    @Autowired
    ElectricVehicleService electricVehicleService;

    @Autowired
    ScheduleService scheduleService;

    @RequestMapping(method = RequestMethod.POST, value = "/saveDV")
    @ResponseBody
    public void saveDV(@RequestBody Driver driver) {
        driverService.saveDriver(driver);
    }
    @RequestMapping(method = RequestMethod.POST, value = "/saveEV")
    @ResponseBody
    public void saveEV(@RequestBody ElectricVehicle electricVehicle,@RequestParam(name = "csid") Integer csid, @RequestParam(name = "driverid") Integer driverid) {
        electricVehicleService.saveEV(electricVehicle,csid,driverid);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/login")
    @ResponseBody
    public Optional<Driver> login(@RequestBody Driver driver) {
        return driverService.login(driver);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/driver")
    @ResponseBody
    public Optional<Driver> getDv(@RequestParam(name = "username") String username) {
        return driverService.findByUsername(username);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/evdriver")
    @ResponseBody
    public ElectricVehicle getEvEV(@RequestParam(name = "username") String username) {
        return electricVehicleService.findByUsername(username);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/editev")
    @ResponseBody
    public ElectricVehicle editEV(@RequestBody ElectricVehicle electricVehicle,@RequestParam(name = "username") String username) {
        return electricVehicleService.editVehicle(electricVehicle,username);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/saveev")
    @ResponseBody
    public ElectricVehicle saveEV(@RequestBody ElectricVehicle electricVehicle,@RequestParam(name = "username") String username) {
        return electricVehicleService.saveVehicle(electricVehicle,username);
    }

    /*@RequestMapping(method = RequestMethod.POST, value = "/savesch")
    @ResponseBody
    public ElectricVehicleChargedValue saveSCH(@RequestParam(name = "carid") Integer carid,@RequestParam(name = "valueCharged") Integer valueCharged) {
        return scheduleService.saveSchedule(carid,valueCharged,8,);
    }*/
}
