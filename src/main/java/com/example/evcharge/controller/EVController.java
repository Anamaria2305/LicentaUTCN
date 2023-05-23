package com.example.evcharge.controller;


import com.example.evcharge.helper.ChargingStationsService;
import com.example.evcharge.helper.DataGathering;
import com.example.evcharge.helper.EVhelper;
import com.example.evcharge.helper.WhaleSolutionParser;
import com.example.evcharge.models.ChargingStation;
import com.example.evcharge.models.ElectricVehicle;
import com.example.evcharge.models.ElectricVehicleChargedValue;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    private EVhelper eVhelper= new EVhelper();

    private ArrayList<Double> ediffList = new ArrayList<>();

    @RequestMapping(method = RequestMethod.GET, value = "/allev")
    @ResponseBody
    public List<ElectricVehicle> getAllEv() {
        return dataGathering.collectData();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/ediff")
    @ResponseBody
    public List<Double> getEdiff(@RequestParam(name = "plugs") Integer plugs,@RequestParam(name = "startTime") Integer startTime) throws IOException {
        ediffList = dataGathering.collectEdiff(plugs,startTime);
        return ediffList;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/allcs")
    @ResponseBody
    public List<ChargingStation> getAllCs() throws IOException {
        return chargingStationsService.getAll();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/sol")
    @ResponseBody
    public List<Pair<ElectricVehicleChargedValue,Integer>> getSol() throws IOException {
        List<ChargingStation> chargingStationList = chargingStationsService.getAll();
        List<ElectricVehicle> electricVehicleList = dataGathering.collectData();
        return  whaleSolutionParser.parseSolution( eVhelper.whaleOptimizationAlgorithm(1500,4,5,chargingStationList,electricVehicleList,ediffList));
    }

}
