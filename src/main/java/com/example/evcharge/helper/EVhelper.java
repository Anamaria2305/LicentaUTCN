package com.example.evcharge.helper;

import com.example.evcharge.models.ChargingStation;
import com.example.evcharge.models.ElectricVehicle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EVhelper {

    public List<ElectricVehicle[][]> generateRandom(Integer sampleSize, Integer plugs, Integer timeSlots, List<ElectricVehicle> electricVehicleList) {
        List<ElectricVehicle[][]> randomList = new ArrayList<>();
        for (int i = sampleSize; i > 0; i--) {
            ElectricVehicle[][] solution = new ElectricVehicle[plugs][timeSlots];
            for (ElectricVehicle ev : electricVehicleList) {
                Random random = new Random();
                int k = random.nextInt(plugs);
                int j = random.nextInt(timeSlots);
                if (solution[k][j] != null) {
                    double minChargeCap = ev.getMinSOC() * ev.getMaxCapacity() / 100;
                    double currentValueCharged = ev.getSOCcurrent() * ev.getMaxCapacity() / 100;
                    double minBound = minChargeCap - currentValueCharged;
                    double maxBoundx = ev.getMaxCapacity() - currentValueCharged;
                    Random rn = new Random();
                    ev.setValueCharged(rn.nextDouble(minBound, maxBoundx));
                    solution[k][j] = ev;
                    ev.setValueCharged(null);
                }
            }
            randomList.add(solution);
        }
        return randomList;
    }

    /**
     *
     * @param mat
     * @param plugs
     * @param timeSlots
     * @return the maximum value from the matrix, but taking into account the ABSOLUTE value
     */
    public float findMax(float mat[][],Integer plugs,Integer timeSlots)
    {
        float maxElement = Float.MIN_VALUE;
        for (int i = 0; i < plugs; i++) {
            for (int j = 0; j < timeSlots; j++) {
                if (mat[i][j]*mat[i][j] > maxElement) {
                    maxElement = mat[i][j];
                }
            }
        }
        return maxElement;
    }
    public List<ElectricVehicle[][]> generateEdiff(Integer sampleSize, Integer plugs, Integer timeSlots, List<ElectricVehicle> electricVehicleList, float[][] ediffMatrix) {
        List<ElectricVehicle[][]> ediffList = new ArrayList<>();
        for(int i=sampleSize;i>0;i--){
            //going through the ediff matrix
            for (int k = 0; k < plugs; k++) {
                for (int j = 0; j < timeSlots; j++) {
                    //finding maxElement at each step
                    float maxElement=findMax(ediffMatrix,plugs,timeSlots);
                    //replace with 0 the value of the max to be able to get the next one
                    int m=0;
                    int n=0;
                    for (m = 0; m < plugs; m++) {
                        for (n = 0; n < timeSlots; n++) {
                            if(ediffMatrix[m][n]==maxElement){
                                ediffMatrix[m][n]=0;
                            }
                        }
                    }

                    for (ElectricVehicle ev: electricVehicleList) {
                        if(maxElement>0){
                            //check car who can charge
                        }
                        else{
                            //check cars who can discharge
                        }
                    }

                }
            }


        }
        return ediffList;
    }

//    TODO change this when we have function for production and consumption
    public float[][] calculateEdiff(Integer plugs, Integer timeSlots){
        float [][] ediffMatrix=new float[plugs][timeSlots];
        Random r=new Random();
        for(int i=0;i<plugs;i++)
        {
            for(int j=0;j<timeSlots;j++) {
                ediffMatrix[i][j] = r.nextFloat(-200,200);
            }
        }
        return ediffMatrix;
    }
    public List<ElectricVehicle[][]> generateInitialSolutionSet(String strategy, Integer sampleSize, List<ChargingStation> chargingStationList,
                                                                Integer timeSlots, List<ElectricVehicle> electricVehicleList) {
        int rows = 0;
        for (ChargingStation cs : chargingStationList) {
            rows += cs.getPlugIds().size();
        }

        List<ElectricVehicle[][]> initialPopulation = new ArrayList<>();

        if (strategy.equals("random")) {
            initialPopulation = generateRandom(sampleSize, rows, timeSlots, electricVehicleList);
        } else if (strategy.equals("ediff")) {
            float[][] ediffMatrix = calculateEdiff(rows,timeSlots);
            initialPopulation = generateEdiff(sampleSize, rows
                    , timeSlots, electricVehicleList, ediffMatrix);
        } else {

        }
        return initialPopulation;
    }
}
