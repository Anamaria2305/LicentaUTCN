package com.example.evcharge.helper;

import com.example.evcharge.models.ChargingStation;
import com.example.evcharge.models.ElectricVehicle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class EVhelper {

    public List<ElectricVehicle[][]> generateRandom(Integer sampleSize, Integer plugs, Integer timeSlots, List<ElectricVehicle> electricVehicleList) {
        List<ElectricVehicle[][]> randomList = new ArrayList<>();
        for (int i = sampleSize; i > 0; i--) {
            ElectricVehicle[][] solution = new ElectricVehicle[plugs][timeSlots];
            List<ElectricVehicle> copyElectricVehicleList = new ArrayList<>();
            copyElectricVehicleList.addAll(electricVehicleList);
            while (!copyElectricVehicleList.isEmpty()) {
                Random random = new Random();
                Integer index = random.nextInt(copyElectricVehicleList.size());
                ElectricVehicle ev = copyElectricVehicleList.get(index);
                int k = random.nextInt(plugs);
                int j = random.nextInt(timeSlots);
                if (solution[k][j] == null) {
                    double minChargeCap = ev.getMinSOC() * ev.getMaxCapacity() / 100;
                    double currentValueCharged = ev.getSOCcurrent() * ev.getMaxCapacity() / 100;
                    double minBound = minChargeCap - currentValueCharged;
                    double maxBoundx = ev.getMaxCapacity() - currentValueCharged;
                    Random rn = new Random();
                    Integer valueCharged = rn.nextInt((int) minBound, (int) maxBoundx);
                    ev.setValueCharged(valueCharged);
                    solution[k][j] = ev;
                    // ev.setValueCharged(null);
                    copyElectricVehicleList.remove(ev);
                }
            }
            randomList.add(solution);
        }
        return randomList;
    }

    /**
     * @param mat
     * @param plugs
     * @param timeSlots
     * @return the maximum value from the array of ediff, but taking into account the ABSOLUTE value
     */
    public float findMax(float mat[][], Integer plugs, Integer timeSlots) {
        float maxElement = Float.MIN_VALUE;
        for (int i = 0; i < plugs; i++) {
            for (int j = 0; j < timeSlots; j++) {
                if (mat[i][j] * mat[i][j] > maxElement) {
                    maxElement = mat[i][j];
                }
            }
        }
        return maxElement;
    }

    public List<ElectricVehicle[][]> generateEdiff(Integer sampleSize, Integer plugs, Integer timeSlots, List<ElectricVehicle> electricVehicleList, ArrayList<Double> ediffMatrix) {
        List<ElectricVehicle[][]> ediffList = new ArrayList<>();
        /*for(int i=sampleSize;i>0;i--){
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


        }*/
        return ediffList;
    }

//    TODO change this when we have function for production and consumption

    /**
     * @param timeSlots - the number of time slots for which we have to generate the differences between
     *                  the consumed energy and produced energy
     * @return
     */
    public ArrayList<Double> calculateEdiff(Integer timeSlots) {
        ArrayList<Double> ediffMatrix = new ArrayList<>();
        Random r = new Random();
        for (int i = 0; i < timeSlots; i++) {
            ediffMatrix.add(r.nextDouble(-150, 150));
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
            ArrayList<Double> ediffMatrix = calculateEdiff(timeSlots);
            initialPopulation = generateEdiff(sampleSize, rows
                    , timeSlots, electricVehicleList, ediffMatrix);
        } else {

        }
        return initialPopulation;
    }


    public Double fitnessFunction(ElectricVehicle[][] electricVehicles, ArrayList<Double> ediff, ArrayList<Double> weights) {
        Double score = 0.0;
        Double ediffBalance = 0.0;
        Double distanceConstraint = 0.0;
        Double timeConstraint = 0.0;
        for (int row = 0; row < electricVehicles.length; row++) {
            for (int col = 0; col < electricVehicles[row].length; col++) {
                if (electricVehicles[row][col] != null) {
                    ediffBalance = ediffBalance + electricVehicles[row][col].getValueCharged();
                    if (!electricVehicles[row][col].getFavouriteChargingStation().getPlugIds().contains(row)) {
                        distanceConstraint++;
                    }
                    if (!electricVehicles[row][col].getFavouriteTimeSlots().contains(col + 1)) {
                        timeConstraint++;
                    }
                }
            }
        }
        for (Double diff : ediff) {
            ediffBalance = ediffBalance + diff;
        }
        if (weights.isEmpty()) {
            score = Math.abs(ediffBalance) + distanceConstraint + timeConstraint;
        } else {
            score = weights.get(0) * Math.abs(ediffBalance) + weights.get(1) * distanceConstraint + weights.get(2) * timeConstraint;
        }
//        System.out.println("Fitness score: " + score + ";\n");
        return score;
    }

    public ElectricVehicle[][] encirclingPrey(Double C, Double A, final ElectricVehicle[][] Xbest, ElectricVehicle[][] Xcurrent,
                                              Integer timeSlots, Integer plugs) {
        double[][] D = new double[plugs][timeSlots];
        ElectricVehicle[][] Xtplus1 = Xcurrent;
        for (int i = 0; i < plugs; i++) {
            for (int j = 0; j < timeSlots; j++) {
                if (Xbest[i][j] != null && Xcurrent[i][j] != null) {
                    D[i][j] = Math.abs(C * Xbest[i][j].getValueCharged() - Xcurrent[i][j].getValueCharged());
                }
            }
        }
        for (int k = 0; k < plugs; k++) {
            for (int l = 0; l < timeSlots; l++) {
                if (Xbest[k][l] != null && D[k][l] != 0 && Xtplus1[k][l] != null) {
                    Xtplus1[k][l].setValueCharged((int) (Xbest[k][l].getValueCharged() + A * D[k][l] + 0.5));
                }
            }
        }
        return Xtplus1;
    }

    public ElectricVehicle[][] bubbleNetAttacking(final ElectricVehicle[][] Xbest, ElectricVehicle[][] Xcurrent,
                                                  Integer timeSlots, Integer plugs) {
        double[][] D = new double[plugs][timeSlots];
        ElectricVehicle[][] Xtplus1 = Xcurrent;
        for (int i = 0; i < plugs; i++) {
            for (int j = 0; j < timeSlots; j++) {
                if (Xbest[i][j] != null && Xcurrent[i][j] != null) {
                    D[i][j] = Math.abs(Xbest[i][j].getValueCharged() - Xcurrent[i][j].getValueCharged());
                }
            }
        }
        Random random = new Random();
        double l = random.nextDouble(-1, 1);
        for (int k = 0; k < plugs; k++) {
            for (int m = 0; m < timeSlots; m++) {
                if (Xbest[k][m] != null && D[k][m] != 0 && Xtplus1[k][m] != null) {
                    Xtplus1[k][m].setValueCharged((int) (D[k][m] * Math.exp(l) * Math.cos(2 * Math.PI + l) + Xbest[k][m].getValueCharged() + 0.5));
                }
            }
        }
        return Xtplus1;
    }

    public ElectricVehicle[][] searchForPrey(Double C, Double A, ElectricVehicle[][] Xrand, ElectricVehicle[][] Xcurrent,
                                             Integer timeSlots, Integer plugs) {
        double[][] D = new double[plugs][timeSlots];
        ElectricVehicle[][] Xtplus1 = new ElectricVehicle[plugs][timeSlots];
        for (int i = 0; i < plugs; i++) {
            for (int j = 0; j < timeSlots; j++) {
                if (Xrand[i][j] != null && Xcurrent[i][j] != null) {
                    D[i][j] = Math.abs(C * Xrand[i][j].getValueCharged() - Xcurrent[i][j].getValueCharged());
                }
            }
        }
        for (int k = 0; k < plugs; k++) {
            for (int l = 0; l < timeSlots; l++) {
                if (Xrand[k][l] != null && D[k][l] != 0 && Xtplus1[k][l] != null) {
                    Xtplus1[k][l].setValueCharged((int) (Xrand[k][l].getValueCharged() + A * D[k][l] + 0.5));
                }
            }
        }
        return Xtplus1;
    }

    public ElectricVehicle[][] whaleOptimizationAlgorithm(int maxt, int plugs, int timeSlots, List<ChargingStation> chargingStationList,
                                                          List<ElectricVehicle> electricVehicleList) {
        List<ElectricVehicle[][]> initialPopulation = generateInitialSolutionSet("random", 50, chargingStationList,
                timeSlots, electricVehicleList);
        ArrayList<Double> ediffList = calculateEdiff(3);
        ArrayList<Double> weights = new ArrayList<>(Arrays.asList(0.4, 0.2, 0.2));
        Double minScore = Double.MAX_VALUE;
        ElectricVehicle[][] Xbest = new ElectricVehicle[plugs][timeSlots];
        for (ElectricVehicle[][] solution1 : initialPopulation) {
            Double currentScore = fitnessFunction(solution1, ediffList, weights);
            if (currentScore < minScore) {
                for (int i = 0; i < plugs; i++) {
                    for (int j = 0; j < timeSlots; j++) {
                        if (solution1[i][j] != null) {
                            Xbest[i][j] = solution1[i][j].deepCopy();
                        }else{
                            Xbest[i][j] = null;
                        }
                    }
                }
                //Xbest = solution;
                minScore = currentScore;
            }
        }
        System.out.println("Best solution initially is " + Arrays.deepToString(Xbest));
        System.out.println(fitnessFunction(Xbest, ediffList, weights));
        int t = 0;
        ElectricVehicle[][] Xbestcopy1 = new ElectricVehicle[plugs][timeSlots];
        for (int i = 0; i < plugs; i++) {
            for (int j = 0; j < timeSlots; j++) {
                if (Xbest[i][j] != null) {
                    Xbestcopy1[i][j] = Xbest[i][j].deepCopy();
                }
            }
        }
        ElectricVehicle[][] Xbestcopy2 = new ElectricVehicle[plugs][timeSlots];
        for (int i = 0; i < plugs; i++) {
            for (int j = 0; j < timeSlots; j++) {
                if (Xbest[i][j] != null) {
                    Xbestcopy2[i][j] = Xbest[i][j].deepCopy();
                }
            }
        }

        while (t < maxt) {
            double a = (double) (2 * (1 - t / maxt));
            for (ElectricVehicle[][] solution : initialPopulation) {
                Random r = new Random();
                Double A = 2 * a * r.nextDouble(0, 1) - a;
                Double C = 2 * r.nextDouble(0, 1);
                Double p = r.nextDouble(0, 1);

                if (p < 0.5) {
                    if (Math.abs(A) < 1) {
                        solution = encirclingPrey(C, A, Xbestcopy1, solution, timeSlots, plugs);
                    } else {
                        Integer index = r.nextInt(initialPopulation.size());
                        ElectricVehicle[][] Xrand = initialPopulation.get(index);
                        ElectricVehicle[][] Xrandcopy = new ElectricVehicle[plugs][timeSlots];
                        for (int i = 0; i < plugs; i++) {
                            for (int j = 0; j < timeSlots; j++) {
                                if (Xrand[i][j] != null) {
                                    Xrandcopy[i][j] = Xrand[i][j].deepCopy();
                                }
                            }
                        }
                        solution = searchForPrey(C, A, Xrandcopy, solution, timeSlots, plugs);
                    }
                } else {
                    solution = bubbleNetAttacking(Xbestcopy2, solution, timeSlots, plugs);
                }
            }
            // beyond search space check


            for (ElectricVehicle[][] solution2 : initialPopulation) {
                Double currentScoreAfterUpdates = fitnessFunction(solution2, ediffList, weights);
                if (currentScoreAfterUpdates < minScore) {
                    for (int i = 0; i < plugs; i++) {
                        for (int j = 0; j < timeSlots; j++) {
                            if (solution2[i][j] != null) {
                                Xbest[i][j] = solution2[i][j].deepCopy();
                            }
                            else{
                                Xbest[i][j] = null;
                            }
                        }
                    }
                    minScore = currentScoreAfterUpdates;
                }
            }
            t++;
        }
        System.out.println("Best solution is " + Arrays.deepToString(Xbest));
        fitnessFunction(Xbest, ediffList, weights);
        return Xbest;
    }
}

