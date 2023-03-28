package com.example.evcharge.helper;

import com.example.evcharge.models.ChargingStation;
import com.example.evcharge.models.ElectricVehicle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EVhelper {

  public List<ElectricVehicle[][]> generateRandom(Integer sampleSize,Integer plugs,Integer timeSlots, List<ElectricVehicle> electricVehicleList){
      List<ElectricVehicle[][]> randomList=new ArrayList<>();
      for(int i=sampleSize;i>0;i--){
          ElectricVehicle [][] solution=new ElectricVehicle [plugs][timeSlots];
          for (ElectricVehicle ev: electricVehicleList) {
              Random random = new Random();
              int k=random.nextInt(plugs);
              int j=random.nextInt(timeSlots);
              if(solution[k][j]!=null){
                  double minChargeCap=ev.getMinSOC()* ev.getMaxCapacity()/100;
                  double currentValueCharged=ev.getSOCcurrent()*ev.getMaxCapacity()/100;
                  double minBound=minChargeCap-currentValueCharged;
                  double maxBoundx=ev.getMaxCapacity()-currentValueCharged;
                  Random rn = new Random();
                  ev.setValueCharged(rn.nextDouble(minBound,maxBoundx));
                  solution[k][j]=ev;
                  ev.setValueCharged(null);
              }
          }
          randomList.add(solution);
      }
      return randomList;
  }

  public List<ElectricVehicle[][]> generateInitialSolutionSet(String strategy, Integer sampleSize, List<ChargingStation> chargingStationList,
                                                  Integer timeSlots, List<ElectricVehicle> electricVehicleList){
      int rows=0;
      for (ChargingStation cs:chargingStationList) {
          rows+=cs.getPlugIds().size();
      }

      List<ElectricVehicle[][]> initialPopulation=new ArrayList<>();

      if(strategy.equals("random")){
        initialPopulation=generateRandom(sampleSize,rows,timeSlots,electricVehicleList);
      }
      else if(strategy.equals("ediff")){

      }
      else{

      }
      return initialPopulation;
  }
}
