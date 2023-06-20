package com.example.evcharge.controller;


import com.example.evcharge.helper.*;
import com.example.evcharge.models.ChargingStation;
import com.example.evcharge.models.ElectricVehicle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/ev")
public class EVController {

    @Autowired
    DataGathering dataGathering;

    @Autowired
    ChargingStationsService chargingStationsService;

    @Autowired
    WhaleSolutionParser whaleSolutionParser;

    @Autowired
    ExperimentClass experimentClass;
    private EVhelper eVhelper = new EVhelper();

    @RequestMapping(method = RequestMethod.GET, value = "/allev")
    @ResponseBody
    public List<ElectricVehicle> getAllEv() {
        return dataGathering.collectData(10);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/ediff")
    @ResponseBody
    public List<Double> getEdiff(@RequestParam(name = "plugs") Integer plugs, @RequestParam(name = "startTime") Integer startTime,@RequestParam(name = "chargeType") String chargeType) throws IOException {
        return dataGathering.collectEdiff(plugs, startTime,chargeType);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/allcs")
    @ResponseBody
    public List<ChargingStation> getAllCs() throws IOException {
        return chargingStationsService.getAll();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/sol")
    @ResponseBody
    public List<List<Object>> getSol(@RequestParam(name = "timeSlots") Integer timeSlots, @RequestParam(name = "startTime") Integer startTime,@RequestParam(name = "chargeType") String chargeType
    ,@RequestParam(name = "maxCars") Integer maxCars,@RequestParam(name = "sampleSize") Integer sampleSize) throws IOException {
        List<ChargingStation> chargingStationList = chargingStationsService.getAll();
        int plugs =0;
        for (ChargingStation cs: chargingStationList) {
            plugs+=cs.getPlugIds().size();
        }
        List<ElectricVehicle> electricVehicleList = dataGathering.collectData(maxCars);
        ArrayList<Double>ediffList = dataGathering.collectEdiff(timeSlots, startTime,chargeType);
        return whaleSolutionParser.parseSolution(eVhelper.whaleOptimizationAlgorithm(100, plugs, timeSlots, chargingStationList, electricVehicleList, ediffList,sampleSize,chargeType),startTime);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/exp")
    @ResponseBody
    public List<List<Object>> getExperiment(@RequestParam(name = "timeSlots") Integer timeSlots, @RequestParam(name = "startTime") Integer startTime,@RequestParam(name = "chargeType") String chargeType
            ,@RequestParam(name = "maxCars") Integer cars,Integer chargingStations) throws IOException {
        return experimentClass.getExperiment(chargingStations,timeSlots,cars,startTime,chargeType);
    }

}
