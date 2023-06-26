package com.example.evcharge.controller;


import com.example.evcharge.helper.*;
import com.example.evcharge.models.ChargingStation;
import com.example.evcharge.models.ElectricVehicle;
import com.example.evcharge.models.ElectricVehicleChargedValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
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

    @Autowired
    ScheduleService scheduleService;
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

    @RequestMapping(method = RequestMethod.GET, value = "/ediffexp")
    @ResponseBody
    public List<Double> getEdiffExp(@RequestParam(name = "plugs") Integer plugs, @RequestParam(name = "startTime") Integer startTime,@RequestParam(name = "chargeType") String chargeType,
                                    @RequestParam(name = "nbev") Integer nbev) throws IOException {
        int dividing = 100;
        if(nbev < 51 && nbev >25){
            dividing =200;
        }
        if(nbev < 25){
            dividing =400;
        }

        return dataGathering.collectEdiffEXP(plugs, startTime,chargeType,dividing);
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

    @RequestMapping(method = RequestMethod.GET, value = "/solgeneral")
    @ResponseBody
    public List<List<Object>> getSolGen() throws IOException {
        List<ChargingStation> chargingStationList = chargingStationsService.getAll();
        int plugs =0;
        for (ChargingStation cs: chargingStationList) {
            plugs+=cs.getPlugIds().size();
        }
        List<ElectricVehicle> electricVehicleList = dataGathering.collectDataNoLimit();
        ArrayList<Double>ediffList = dataGathering.collectEdiff(10, 8,"Charge");
        whaleSolutionParser.parseSolution(eVhelper.whaleOptimizationAlgorithm(100, plugs, 10, chargingStationList, electricVehicleList, ediffList,50,"Charge"),8);
        return whaleSolutionParser.parseSolution(eVhelper.whaleOptimizationAlgorithm(100, plugs, 10, chargingStationList, electricVehicleList, ediffList,50,"Charge"),8);
    }
    @RequestMapping(method = RequestMethod.GET, value = "/exp")
    @ResponseBody
    public List<List<Object>> getExperiment(@RequestParam(name = "timeSlots") Integer timeSlots, @RequestParam(name = "startTime") Integer startTime,@RequestParam(name = "chargeType") String chargeType
            ,@RequestParam(name = "maxCars") Integer cars,Integer chargingStations) throws IOException {
        return experimentClass.getExperiment(chargingStations,timeSlots,cars,startTime,chargeType);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/finalize")
    @ResponseBody
    public List<ElectricVehicleChargedValue>  finalizeSchedule(@RequestBody List<ElectricVehicleChargedValue> solution) throws IOException {
        return scheduleService.saveAll(solution);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/notification")
    @ResponseBody
    public List<Object>  notification(@RequestParam(name = "username") String username) throws IOException {
        return scheduleService.getNotification(username);
    }

    @PostMapping("/upload")
    public List<List<Object>> uploadFile(@RequestParam("file") MultipartFile file,
      @RequestParam(name = "startTime") Integer startTime,@RequestParam(name = "chargeType") String chargeType) throws IOException {
        if (file.isEmpty()) {
            return null;
        }
        return experimentClass.getExperimentWithCSV(startTime,chargeType,file);
    }

    @PostMapping("/download")
    public ResponseEntity<ByteArrayResource> downloadFile(@RequestBody List<ElectricVehicleChargedValue> solution,
    @RequestParam(name = "csnum") Integer csnum,@RequestParam(name = "window") Integer window) {
        String fileContent = experimentClass.generateCsvContent(solution,csnum,window);
        byte[] fileBytes = fileContent.getBytes();
        ByteArrayResource resource = new ByteArrayResource(fileBytes);
        HttpHeaders headers = new HttpHeaders();
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String fileName = "experimentData"+currentDate.format(formatter);
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName + ".csv");
        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/ediffExpUpl")
    @ResponseBody
    public List<Double> getEdiff(@RequestParam("file") MultipartFile file,@RequestParam(name = "startTime") Integer startTime,@RequestParam(name = "chargeType") String chargeType) throws IOException {
        return dataGathering.collectEdiffCSV(file, startTime,chargeType);
    }

}
