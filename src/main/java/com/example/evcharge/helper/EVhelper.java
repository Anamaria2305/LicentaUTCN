package com.example.evcharge.helper;

import com.example.evcharge.models.ChargingStation;
import com.example.evcharge.models.ElectricVehicle;
import com.example.evcharge.models.ElectricVehicleChargedValue;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class EVhelper {

    private ElectricVehicleChargedValue[][] deepCopy(Integer plugs, Integer timeSlots, ElectricVehicleChargedValue[][] originalEVCV) {
        ElectricVehicleChargedValue[][] copyEVCV = new ElectricVehicleChargedValue[plugs][timeSlots];
        for (int k = 0; k < plugs; k++) {
            for (int l = 0; l < timeSlots; l++) {
                if (originalEVCV[k][l] != null) {
                    copyEVCV[k][l] = originalEVCV[k][l].deepCopy();
                }
            }
        }
        return copyEVCV;
    }

    public List<ElectricVehicleChargedValue[][]> generateRandom(Integer sampleSize, Integer plugs, Integer timeSlots, List<ElectricVehicle> electricVehicleList) {
        List<ElectricVehicleChargedValue[][]> initialPopulationRandomList = new ArrayList<>();
        for (int i = sampleSize; i > 0; i--) {
            ElectricVehicleChargedValue[][] solution = new ElectricVehicleChargedValue[plugs][timeSlots];
            List<ElectricVehicle> copyElectricVehicleList = new ArrayList<>();
            copyElectricVehicleList.addAll(electricVehicleList);
            while (!copyElectricVehicleList.isEmpty()) {
                Random random = new Random();
                Integer index = random.nextInt(copyElectricVehicleList.size());
                ElectricVehicle ev = copyElectricVehicleList.get(index);
                int k = random.nextInt(plugs);
                int j = random.nextInt(timeSlots);
                if (solution[k][j] == null) {
                    double minChargeCapacity = ev.getMinSOC() * ev.getMaxCapacity() / 100;
                    double currentValueCharged = ev.getSOCcurrent() * ev.getMaxCapacity() / 100;

                    double minBound = minChargeCapacity - currentValueCharged;
                    double maxBound = ev.getMaxCapacity() - currentValueCharged;
                    if (Math.abs(minBound) > ev.getChrDisPerHour()) {
                        minBound = (-1) * ev.getChrDisPerHour();
                    }

                    if (maxBound > ev.getChrDisPerHour()) {
                        maxBound = ev.getChrDisPerHour();
                    }

                    Random rn = new Random();
                    Integer valueCharged = rn.nextInt((int) minBound, (int) maxBound);
                    ElectricVehicleChargedValue electricVehicleChargedValue = new ElectricVehicleChargedValue(ev, valueCharged);
                    solution[k][j] = electricVehicleChargedValue;
                    copyElectricVehicleList.remove(ev);
                }
            }
            initialPopulationRandomList.add(solution);
        }
        return initialPopulationRandomList;
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
        /*for (int i = 0; i < timeSlots; i++) {
            ediffMatrix.add(r.nextDouble(-150, 150));
        }*/
        ediffMatrix.add(150.0);
        ediffMatrix.add(-120.0);
        ediffMatrix.add(140.0);
        return ediffMatrix;
    }

    public List<ElectricVehicleChargedValue[][]> generateInitialSolutionSet(String strategy, Integer sampleSize, List<ChargingStation> chargingStationList,
                                                                            Integer timeSlots, List<ElectricVehicle> electricVehicleList) {
        int rows = 0;
        for (ChargingStation cs : chargingStationList) {
            rows += cs.getPlugIds().size();
        }

        List<ElectricVehicleChargedValue[][]> initialPopulation = new ArrayList<>();

        if (strategy.equals("random")) {
            initialPopulation = generateRandom(sampleSize, rows, timeSlots, electricVehicleList);
        } else if (strategy.equals("ediff")) {
            ArrayList<Double> ediffMatrix = calculateEdiff(timeSlots);
           /* initialPopulation = generateEdiff(sampleSize, rows
                    , timeSlots, electricVehicleList, ediffMatrix);*/
        } else {

        }
        return initialPopulation;
    }


    public Double fitnessFunction(ElectricVehicleChargedValue[][] electricVehicles, ArrayList<Double> ediff, ArrayList<Double> weightsForFitnessFunction, ArrayList<Double> weightsForPenalty) {
        Double score = 0.0;
        Double ediffBalance = 0.0;
        Double violation = 0.0;
        Double CV = 0.0;
        for (int row = 0; row < electricVehicles.length; row++) {
            for (int col = 0; col < electricVehicles[row].length; col++) {
                if (electricVehicles[row][col] != null) {
                    ediffBalance = ediffBalance + electricVehicles[row][col].getValueCharged();
                    if (!electricVehicles[row][col].getElectricVehicle().getFavouriteChargingStation().getPlugIds().contains(row)) {
                        violation++;
                        CV += electricVehicles[row][col].getElectricVehicle().getConstraintsPenalty().get(0);
                    }
                    if (!electricVehicles[row][col].getElectricVehicle().getFavouriteTimeSlots().contains(col + 1)) {
                        violation++;
                        CV += electricVehicles[row][col].getElectricVehicle().getConstraintsPenalty().get(1);
                    }
                }
            }
        }
        for (Double diff : ediff) {
            ediffBalance = ediffBalance + diff;
        }
        if (weightsForFitnessFunction.isEmpty()) {
            score = Math.abs(ediffBalance) + (weightsForPenalty.get(0) * CV + weightsForPenalty.get(1) * violation);
        } else {
            score = weightsForFitnessFunction.get(0) * Math.abs(ediffBalance) + weightsForFitnessFunction.get(1) * (weightsForPenalty.get(0) * CV + weightsForPenalty.get(1) * violation);
        }
        return score;
    }

    /**
     * @param C
     * @param A
     * @param Xcurrent
     * @param timeSlots
     * @param plugs
     * @return this function has name of both phases because they use a different solution for
     * updating the current one
     */
    public ElectricVehicleChargedValue[][] encirclingPreysearchForPreyElementWise(Double C, Double A, ElectricVehicleChargedValue[][] XbestRand, ElectricVehicleChargedValue[][] Xcurrent,
                                                                                  Integer timeSlots, Integer plugs) {
        double[][] D = new double[plugs][timeSlots];
        for (int i = 0; i < plugs; i++) {
            for (int j = 0; j < timeSlots; j++) {
                if (XbestRand[i][j] != null && Xcurrent[i][j] != null) {
                    D[i][j] = Math.abs(C * XbestRand[i][j].getValueCharged() - Xcurrent[i][j].getValueCharged());
                }
            }
        }
        for (int k = 0; k < plugs; k++) {
            for (int l = 0; l < timeSlots; l++) {
                if (XbestRand[k][l] != null && D[k][l] != 0 && Xcurrent[k][l] != null) {
                    Xcurrent[k][l].setValueCharged((int) (XbestRand[k][l].getValueCharged() + A * D[k][l] + 0.5));
                }
            }
        }
        return Xcurrent;
    }

    public ElectricVehicleChargedValue[][] bubbleNetAttackingElementWise(final ElectricVehicleChargedValue[][] Xbest, ElectricVehicleChargedValue[][] Xcurrent,
                                                                         Integer timeSlots, Integer plugs) {
        double[][] D = new double[plugs][timeSlots];
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
                if (Xbest[k][m] != null && D[k][m] != 0 && Xcurrent[k][m] != null) {
                    Xcurrent[k][m].setValueCharged((int) (D[k][m] * Math.exp(l) * Math.cos(2 * Math.PI + l) + Xbest[k][m].getValueCharged() + 0.5));
                }
            }
        }
        return Xcurrent;
    }

    //implement those ID by ID

    private ElectricVehicleChargedValue[][] checkSearchAgentGoesBeyondSearchSpace(ElectricVehicleChargedValue[][] solution, Integer plugs, Integer timeSlots, ArrayList<Double> ediffList) {
        for (int i = 0; i < plugs; i++) {
            for (int j = 0; j < timeSlots; j++) {
                if (solution[i][j] != null) {
                    //check if there is enough energy
                    /*for (int a = 0; a < timeSlots; a++) {
                        int energyForCars = 0;
                        for (int b = 0; b < plugs; b++) {
                            if(solution[a][b]!=null){
                                energyForCars = energyForCars + solution[j][i].getValueCharged();
                            }
                        }
                        // update this to check if another time is possible
                        if (ediffList.get(a) > 0 && ediffList.get(a) < energyForCars) {
                            while (ediffList.get(a) < energyForCars){
                                for(int c = 0;c < plugs; c++){
                                    if(solution[c][a]!=null){
                                        solution[c][a].setValueCharged(solution[c][a].getValueCharged()-1);
                                        energyForCars--;
                                    }
                                }
                            }
                        }
                    }*/
                    Integer valueCharged = solution[i][j].getValueCharged();
                    //check if value charged is greater than it capacity per hour
                    if (Math.abs(valueCharged) > solution[i][j].getElectricVehicle().getChrDisPerHour()) {
                        if (valueCharged < 0) {
                            solution[i][j].setValueCharged(-1 * solution[i][j].getElectricVehicle().getChrDisPerHour());
                        } else {
                            solution[i][j].setValueCharged(solution[i][j].getElectricVehicle().getChrDisPerHour());
                        }
                    }
                    //check if the soc goes above max or beyond min
                    double minChargeCapacity = (double) solution[i][j].getElectricVehicle().getSOCcurrent() * solution[i][j].getElectricVehicle().getMinSOC() / 100;
                    double currentValueCharged = (double) solution[i][j].getElectricVehicle().getSOCcurrent() * solution[i][j].getElectricVehicle().getMaxCapacity() / 100;
                    if ((valueCharged + currentValueCharged) < minChargeCapacity) {
                        solution[i][j].setValueCharged((int) (minChargeCapacity - currentValueCharged));
                    }
                    if ((valueCharged + currentValueCharged) > solution[i][j].getElectricVehicle().getMaxCapacity()) {
                        solution[i][j].setValueCharged((int) (solution[i][j].getElectricVehicle().getMaxCapacity() - currentValueCharged));
                    }
                }
            }
        }
        return solution;
    }

    public ElectricVehicleChargedValue[][] whaleOptimizationAlgorithm(int maxt, int plugs, int timeSlots, List<ChargingStation> chargingStationList,
                                                                      List<ElectricVehicle> electricVehicleList) throws IOException {
        List<ElectricVehicleChargedValue[][]> initialPopulation = generateInitialSolutionSet("random", 20, chargingStationList,
                timeSlots, electricVehicleList);

        ArrayList<Double> ediffList = calculateEdiff(3);

        ArrayList<Double> weightsForFitness = new ArrayList<>(Arrays.asList(0.6, 0.4));
        ArrayList<Double> weightsForPenalty = new ArrayList<>(Arrays.asList(2.0, 2.0));

        Double minScore = Double.MAX_VALUE;

        ElectricVehicleChargedValue[][] Xbest = new ElectricVehicleChargedValue[plugs][timeSlots];

        ArrayList<Double> bestFitnessAmongEachIteration = new ArrayList<>();
        List<ElectricVehicleChargedValue[][]> bestSolutionEachIteration = new ArrayList<>();

        for (ElectricVehicleChargedValue[][] solution : initialPopulation) {
            Double currentScore = fitnessFunction(solution, ediffList, weightsForFitness, weightsForPenalty);
            if (currentScore < minScore) {
                for (int i = 0; i < plugs; i++) {
                    for (int j = 0; j < timeSlots; j++) {
                        if (solution[i][j] != null) {
                            Xbest[i][j] = solution[i][j].deepCopy();
                        } else {
                            Xbest[i][j] = null;
                        }
                    }
                }
                minScore = currentScore;
            }
        }
        bestFitnessAmongEachIteration.add(minScore);
        bestSolutionEachIteration.add(this.deepCopy(plugs, timeSlots, Xbest));
        System.out.println("Best solution initially is " + Arrays.deepToString(Xbest));
        System.out.println("Fitness score is: " + fitnessFunction(Xbest, ediffList, weightsForFitness, weightsForPenalty));

        int t = 0;
        while (t < maxt) {
            double a = (double) (2 * (1 - t / maxt));
            for (int i = 0; i < initialPopulation.size(); i++) {
                ElectricVehicleChargedValue[][] solution = initialPopulation.get(i);
                Random r = new Random();
                Double A = 2 * a * r.nextDouble(0, 1) - a;
                Double C = 2 * r.nextDouble(0, 1);
                Double p = r.nextDouble(0, 1);
                if (p < 0.5) {
                    if (Math.abs(A) < 1) {
                        solution = encirclingPreysearchForPreyElementWise(C, A, Xbest, solution, timeSlots, plugs);
                    } else {
                        Integer index = r.nextInt(initialPopulation.size());
                        ElectricVehicleChargedValue[][] Xrand = initialPopulation.get(index);
                        ElectricVehicleChargedValue[][] Xrandcopy = this.deepCopy(plugs, timeSlots, Xrand);
                        solution = encirclingPreysearchForPreyElementWise(C, A, Xrandcopy, solution, timeSlots, plugs);
                    }
                } else {
                    solution = bubbleNetAttackingElementWise(Xbest, solution, timeSlots, plugs);
                }
                initialPopulation.set(i, solution);
            }

            // check if any search agent goes beyond search space and amend it
            initialPopulation = initialPopulation.stream() .map(solution -> checkSearchAgentGoesBeyondSearchSpace(solution,plugs,timeSlots,ediffList)) .collect(Collectors.toList());

            double minScoreEachIteration = Double.MAX_VALUE;
            ElectricVehicleChargedValue[][] XbestEachIteration = new ElectricVehicleChargedValue[plugs][timeSlots];
            for (ElectricVehicleChargedValue[][] solution : initialPopulation) {
                Double currentScoreAfterUpdates = fitnessFunction(solution, ediffList, weightsForFitness, weightsForPenalty);
                if (currentScoreAfterUpdates < minScore) {
                    Xbest = this.deepCopy(plugs, timeSlots, solution);
                    minScore = currentScoreAfterUpdates;
                }
                if (currentScoreAfterUpdates < minScoreEachIteration) {
                    XbestEachIteration = this.deepCopy(plugs, timeSlots, solution);
                    minScoreEachIteration = currentScoreAfterUpdates;
                }
            }
            bestFitnessAmongEachIteration.add(minScoreEachIteration);
            bestSolutionEachIteration.add(XbestEachIteration);
            t++;
        }

        System.out.println("Best final solution is " + Arrays.deepToString(Xbest));
        System.out.println("Fitness score for best solution is: " + fitnessFunction(Xbest, ediffList, weightsForFitness, weightsForPenalty));
        this.printFitnessAndSolutionsInFile(bestFitnessAmongEachIteration,bestSolutionEachIteration);
        return Xbest;
    }

    private void printFitnessAndSolutionsInFile(ArrayList<Double> bestFitnessAmongEachIteration,
                                                List<ElectricVehicleChargedValue[][]> bestSolutionEachIteration) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet spreadsheet = workbook.createSheet("FitnessValues");
        XSSFRow row;
        for (int i=0;i<bestFitnessAmongEachIteration.size();i++) {
            row = spreadsheet.createRow(i);
            Cell cell = row.createCell(0);
            cell.setCellValue(bestFitnessAmongEachIteration.get(i));
            System.out.println("\nBest fitness score at iteration "+i+" is: "+bestFitnessAmongEachIteration.get(i));

        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd_M_yyyy_hh_mm_ss");

        FileOutputStream out = new FileOutputStream(
                new File("E:\\Facultate\\UTCN\\An4\\Licenta\\FitnessValue"+sdf.format(new Date())+".xlsx"));

        workbook.write(out);
        out.close();
    }
}

