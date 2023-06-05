package com.example.evcharge.helper;

import com.example.evcharge.models.ChargingStation;
import com.example.evcharge.models.ElectricVehicle;
import com.example.evcharge.models.ElectricVehicleChargedValue;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public List<ElectricVehicleChargedValue[][]> generateRandom(Integer sampleSize, Integer plugs, Integer timeSlots, List<ElectricVehicle> electricVehicleList, ArrayList<Double> ediffList) {
        List<ElectricVehicleChargedValue[][]> initialPopulationRandomList = new ArrayList<>();
        for (int i = sampleSize; i > 0; i--) {
            ElectricVehicleChargedValue[][] solution = new ElectricVehicleChargedValue[plugs][timeSlots];
            List<ElectricVehicle> copyElectricVehicleList = new ArrayList<>();
            copyElectricVehicleList.addAll(electricVehicleList);
            while (!copyElectricVehicleList.isEmpty()) {
                Random random = new Random();
                Integer index = random.nextInt(copyElectricVehicleList.size());
                ElectricVehicle ev = copyElectricVehicleList.get(index);
                int k=-1;
                int j=-1;
                for( int a= 2 * ev.getFavouriteChargingStation().getId()-2;a<=2 * ev.getFavouriteChargingStation().getId()-1;a++){
                    for(int b = 0;b<ev.getFavouriteTimeSlots().size();b++ ){
                        if(solution[a][ev.getFavouriteTimeSlots().get(b)-1] == null){
                            k= a;
                            j= ev.getFavouriteTimeSlots().get(b)-1;
                            break;
                        }
                    }
                }

                if(k==-1 || j==-1){
                    k = random.nextInt(plugs);
                    j = random.nextInt(timeSlots);
                }

                if (solution[k][j] == null) {
                    double minChargeCapacity = (double) (ev.getMinSOC() * ev.getMaxCapacity()) / 100;
                    double currentValueCharged = (double) (ev.getSOCcurrent() * ev.getMaxCapacity()) / 100;
                    double minBound = minChargeCapacity - currentValueCharged;
                    double maxBound = ev.getMaxCapacity() - currentValueCharged;

                    if (Math.abs(minBound) > ev.getChrDisPerHour()) {
                        minBound = (-1) * ev.getChrDisPerHour();
                    }

                    if (maxBound > ev.getChrDisPerHour()) {
                        maxBound = ev.getChrDisPerHour();
                    }

                    if (ediffList.get(j) > 0) {
                        minBound = 1;
                    } else {
                        maxBound = 0;
                    }

                    int valueCharged = 0;
                    while (valueCharged == 0) {
                        Random rn = new Random();
                        valueCharged = rn.nextInt((int) minBound, (int) maxBound + 1);
                    }

                    ElectricVehicleChargedValue electricVehicleChargedValue = new ElectricVehicleChargedValue(ev, valueCharged);
                    solution[k][j] = electricVehicleChargedValue;
                    copyElectricVehicleList.remove(ev);
                }
            }
            initialPopulationRandomList.add(solution);
        }
        return initialPopulationRandomList;
    }

    // in the middle of the day, solar power conists aprox 70% of energy produces

    /**
     * @param timeSlots - the number of time slots for which we have to generate the differences between
     *                  the consumed energy and produced energy
     * @return
     */
    public ArrayList<Double> calculateEdiff(Integer timeSlots) {
        ArrayList<Double> ediffMatrix = new ArrayList<>();
        Random r = new Random();
        ediffMatrix.add(140.0);
        ediffMatrix.add(141.0);
        ediffMatrix.add(136.5);
        ediffMatrix.add(139.0);
        ediffMatrix.add(135.0);
        return ediffMatrix;
    }

    public List<ElectricVehicleChargedValue[][]> generateInitialSolutionSet(String strategy, Integer sampleSize, List<ChargingStation> chargingStationList,
                                                                            Integer timeSlots, List<ElectricVehicle> electricVehicleList, ArrayList<Double> ediffList) {
        int rows = 0;
        for (ChargingStation cs : chargingStationList) {
            rows += cs.getPlugIds().size();
        }

        List<ElectricVehicleChargedValue[][]> initialPopulation = new ArrayList<>();

        if (strategy.equals("random")) {
            initialPopulation = generateRandom(sampleSize, rows, timeSlots, electricVehicleList, ediffList);
        } else if (strategy.equals("ediff")) {
            ArrayList<Double> ediffMatrix = calculateEdiff(timeSlots);
           /* initialPopulation = generateEdiff(sampleSize, rows
                    , timeSlots, electricVehicleList, ediffMatrix);*/
        } else {

        }
        return initialPopulation;
    }

    public Double euclideanDistanceDiversity(List<ElectricVehicleChargedValue[][]> population) {
        double euclideanDistance = 0.0;
        for (int nr = 0; nr < population.size() - 1; nr++) {
            int row = population.get(nr).length;
            int col = population.get(nr)[0].length;
            for (int i = 0; i < row; i++) {
                for (int j = 0; j < col; j++) {
                    if (population.get(nr)[i][j] != null) {
                        int row2 = population.get(nr + 1).length;
                        int col2 = population.get(nr + 1)[0].length;
                        for (int a = 0; a < row; a++) {
                            for (int b = 0; b < col; b++) {
                                if (population.get(nr + 1)[a][b] != null) {
                                    if (population.get(nr + 1)[a][b].getElectricVehicle().getId() == population.get(nr)[i][j].getElectricVehicle().getId()) {
                                        euclideanDistance += (Math.abs(population.get(nr)[i][j].getValueCharged() - population.get(nr + 1)[a][b].getValueCharged()));
                                        euclideanDistance += Math.abs(j - b);
                                        if (population.get(nr + 1)[a][b].getElectricVehicle().getFavouriteChargingStation().getId() != population.get(nr)[i][j].getElectricVehicle().getFavouriteChargingStation().getId()) {
                                            euclideanDistance++;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return euclideanDistance;
    }

    public Double fitnessFunction(ElectricVehicleChargedValue[][] electricVehicles, ArrayList<Double> ediff, ArrayList<Double> weightsForFitnessFunction, ArrayList<Double> weightsForPenalty) {
        double score = 0.0;
        ArrayList<Double> sumOnColumns = Stream.generate(() -> 0.0)
                .limit(electricVehicles[0].length).collect(Collectors.toCollection(ArrayList::new));
        Double violation = 0.0;
        Double CV = 0.0;
        int charginStationViolation = 0;
        for (int row = 0; row < electricVehicles.length; row++) {
            for (int col = 0; col < electricVehicles[row].length; col++) {
                if (electricVehicles[row][col] != null) {

                    Double newSum = sumOnColumns.get(col) + electricVehicles[row][col].getValueCharged();
                    sumOnColumns.set(col, newSum);
                    if (!electricVehicles[row][col].getElectricVehicle().getFavouriteChargingStation().getPlugIds().contains(row)) {
                        violation++;
                        CV += electricVehicles[row][col].getElectricVehicle().getConstraintsPenalty().get(0);
                        charginStationViolation += electricVehicles[row][col].getElectricVehicle().getConstraintsPenalty().get(0);
                    }
                    if (!electricVehicles[row][col].getElectricVehicle().getFavouriteTimeSlots().contains(col + 1)) {
                        violation++;
                        CV += electricVehicles[row][col].getElectricVehicle().getConstraintsPenalty().get(1);
                    }
                }
            }
        }
        Double ediffBalance = 0.0;
        for (int m = 0; m < ediff.size(); m++) {
            ediffBalance = ediffBalance + Math.abs((ediff.get(m) - sumOnColumns.get(m)));
        }
        if (weightsForFitnessFunction.isEmpty()) {
            score = ediffBalance + (weightsForPenalty.get(0) * CV + weightsForPenalty.get(1) * violation);
        } else {
            score = weightsForFitnessFunction.get(0) * Math.abs(ediffBalance) + weightsForFitnessFunction.get(1) * (weightsForPenalty.get(0) * CV + weightsForPenalty.get(1) * violation);
        }
        System.out.println("Chargin Station Violation: " + charginStationViolation);
        System.out.println("Time violation: " + (CV - charginStationViolation));
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

    public ElectricVehicleChargedValue[][] encirclingPreysearchForPreyIdWise(Double C, Double A, ElectricVehicleChargedValue[][] XbestRand, ElectricVehicleChargedValue[][] Xcurrent,
                                                                             Integer timeSlots, Integer plugs) {
        for (int i = 0; i < plugs; i++) {
            for (int j = 0; j < timeSlots; j++) {
                if (Xcurrent[i][j] != null) {

                    for (int a = 0; a < plugs; a++) {
                        boolean breakCondition = false;
                        for (int b = 0; b < timeSlots; b++) {
                            if (XbestRand[a][b] != null) {
                                if (Objects.equals(XbestRand[a][b].getElectricVehicle().getId(), Xcurrent[i][j].getElectricVehicle().getId())) {
                                    Integer valueChargedBestRand = XbestRand[a][b].getValueCharged();
                                    double newValueForXCurrent = valueChargedBestRand - A * (C * valueChargedBestRand - Xcurrent[i][j].getValueCharged()) + 0.5;
                                    Xcurrent[i][j].setValueCharged((int) newValueForXCurrent);
                                    breakCondition = true;
                                    break;
                                }
                            }
                        }
                        if (breakCondition) break;
                    }

                }
            }
        }
        return Xcurrent;
    }

    public ElectricVehicleChargedValue[][] bubbleNetAttackingIdWise(final ElectricVehicleChargedValue[][] Xbest, ElectricVehicleChargedValue[][] Xcurrent,
                                                                    Integer timeSlots, Integer plugs) {
        Random random = new Random();
        double l = random.nextDouble(-1, 1);
        for (int i = 0; i < plugs; i++) {
            for (int j = 0; j < timeSlots; j++) {
                if (Xcurrent[i][j] != null) {

                    for (int a = 0; a < plugs; a++) {
                        boolean breakCondition = false;
                        for (int b = 0; b < timeSlots; b++) {
                            if (Xbest[a][b] != null) {
                                if (Objects.equals(Xbest[a][b].getElectricVehicle().getId(), Xcurrent[i][j].getElectricVehicle().getId())) {
                                    Integer valueChargedBestRand = Xbest[a][b].getValueCharged();
                                    double newValueForXCurrent = (valueChargedBestRand - Xcurrent[i][j].getValueCharged()) * Math.exp(l) * Math.cos(2 * Math.PI + l) + valueChargedBestRand + 0.5;
                                    Xcurrent[i][j].setValueCharged((int) newValueForXCurrent);
                                    breakCondition = true;
                                    break;
                                }
                            }
                        }
                        if (breakCondition) break;
                    }

                }
            }
        }
        return Xcurrent;
    }

    private ElectricVehicleChargedValue[][] checkSearchAgentGoesBeyondSearchSpace(ElectricVehicleChargedValue[][] solution, Integer plugs, Integer timeSlots, ArrayList<Double> ediffList) {
        for (int i = 0; i < plugs; i++) {
            for (int j = 0; j < timeSlots; j++) {
                if (solution[i][j] != null) {
                    //check if value is not 0
                    if (solution[i][j].getValueCharged() == 0) {
                        if (ediffList.get(j) > 0) {
                            solution[i][j].setValueCharged(1);
                        } else {
                            solution[i][j].setValueCharged(-1);
                        }
                    }
                    //check if there is enough energy
                    for (int a = 0; a < timeSlots; a++) {
                        int energyForCars = 0;
                        for (int b = 0; b < plugs; b++) {
                            if (solution[b][a] != null) {
                                energyForCars = energyForCars + solution[b][a].getValueCharged();
                            }
                        }

                        if (ediffList.get(a) > 0 && ediffList.get(a) < energyForCars) {
                            while (ediffList.get(a) < energyForCars) {
                                for (int c = 0; c < plugs; c++) {
                                    if (solution[c][a] != null) {
                                        solution[c][a].setValueCharged(solution[c][a].getValueCharged() - 1);
                                        energyForCars--;
                                    }
                                }
                            }
                        }
                    }
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
                    Integer valueCharged2 = solution[i][j].getValueCharged();
                    double minChargeCapacity = (double) solution[i][j].getElectricVehicle().getSOCcurrent() * solution[i][j].getElectricVehicle().getMinSOC() / 100;
                    double currentValueCharged = (double) solution[i][j].getElectricVehicle().getSOCcurrent() * solution[i][j].getElectricVehicle().getMaxCapacity() / 100;
                    if ((valueCharged2 + currentValueCharged) < minChargeCapacity) {
                        solution[i][j].setValueCharged((int) (minChargeCapacity - currentValueCharged));
                    }
                    if ((valueCharged2 + currentValueCharged) > solution[i][j].getElectricVehicle().getMaxCapacity()) {
                        solution[i][j].setValueCharged((int) (solution[i][j].getElectricVehicle().getMaxCapacity() - currentValueCharged));
                    }
                }
            }
        }
        return solution;
    }

    public ElectricVehicleChargedValue[][] whaleOptimizationAlgorithm(int maxt, int plugs, int timeSlots, List<ChargingStation> chargingStationList,
                                                                      List<ElectricVehicle> electricVehicleList, ArrayList<Double> ediffList,Integer sampleSize) throws IOException {
        Instant start = Instant.now();
        int iterationsSinceLastChange = 0;
        List<ElectricVehicleChargedValue[][]> initialPopulation = generateInitialSolutionSet("random", sampleSize, chargingStationList,
                timeSlots, electricVehicleList, ediffList);
        ArrayList<Double> weightsForFitness = new ArrayList<>(Arrays.asList(0.6, 0.4));
        ArrayList<Double> weightsForPenalty = new ArrayList<>(Arrays.asList(2.0, 2.0));

        Double minScore = Double.MAX_VALUE;

        ElectricVehicleChargedValue[][] Xbest = new ElectricVehicleChargedValue[plugs][timeSlots];

        ArrayList<Double> bestFitnessAmongEachIteration = new ArrayList<>();
        List<ElectricVehicleChargedValue[][]> bestSolutionEachIteration = new ArrayList<>();
        ArrayList<Double> euclideanDistanceForPopulationEachIteration = new ArrayList<>();

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
        euclideanDistanceForPopulationEachIteration.add(euclideanDistanceDiversity(initialPopulation));
        System.out.println("Best solution initially is " + Arrays.deepToString(Xbest));
        System.out.println("Fitness score is: " + fitnessFunction(Xbest, ediffList, weightsForFitness, weightsForPenalty));
        System.out.println("Euclidean distance of initial population is: " + euclideanDistanceDiversity(initialPopulation));
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
                        //solution = encirclingPreysearchForPreyIdWise(C, A, Xbest, solution, timeSlots, plugs);
                    } else {
                        Integer index = r.nextInt(initialPopulation.size());
                        ElectricVehicleChargedValue[][] Xrand = initialPopulation.get(index);
                        ElectricVehicleChargedValue[][] Xrandcopy = this.deepCopy(plugs, timeSlots, Xrand);
                        solution = encirclingPreysearchForPreyElementWise(C, A, Xrandcopy, solution, timeSlots, plugs);
                        //solution = encirclingPreysearchForPreyIdWise(C, A, Xrandcopy, solution, timeSlots, plugs);
                    }
                } else {
                    solution = bubbleNetAttackingElementWise(Xbest, solution, timeSlots, plugs);
                    //solution = bubbleNetAttackingIdWise(Xbest, solution, timeSlots, plugs);
                }
                initialPopulation.set(i, solution);
            }

            // check if any search agent goes beyond search space and amend it
            initialPopulation = initialPopulation.stream().map(solution -> checkSearchAgentGoesBeyondSearchSpace(solution, plugs, timeSlots, ediffList)).collect(Collectors.toList());
            euclideanDistanceForPopulationEachIteration.add(euclideanDistanceDiversity(initialPopulation));
            for (ElectricVehicleChargedValue[][] solution : initialPopulation) {
                Double currentScoreAfterUpdates = fitnessFunction(solution, ediffList, weightsForFitness, weightsForPenalty);
                if (currentScoreAfterUpdates < minScore) {
                    Xbest = this.deepCopy(plugs, timeSlots, solution);
                    minScore = currentScoreAfterUpdates;
                }
            }
            if (minScore == bestFitnessAmongEachIteration.get(bestFitnessAmongEachIteration.size() - 1)) {
                iterationsSinceLastChange++;
            } else {
                iterationsSinceLastChange = 0;
            }
            bestFitnessAmongEachIteration.add(minScore);
            bestSolutionEachIteration.add(this.deepCopy(plugs, timeSlots, Xbest));
            t++;
            if (iterationsSinceLastChange > 10) {
                break;
            }
        }

        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();
        System.out.println("Time elapsed: "+timeElapsed);
        System.out.println("Best final solution is " + Arrays.deepToString(Xbest));
        System.out.println("Fitness score for best solution is: " + fitnessFunction(Xbest, ediffList, weightsForFitness, weightsForPenalty));
        System.out.println("Euclidean distance of initial population is: " + euclideanDistanceDiversity(initialPopulation));
        this.printFitnessAndSolutionsInFile(bestFitnessAmongEachIteration);
        this.printEuclideanDistance(euclideanDistanceForPopulationEachIteration);
        ArrayList<Double> ediffNew = this.calculateNewEdiff(Xbest, ediffList, plugs, timeSlots);
        ArrayList<Integer> valueChargedByCars = new ArrayList<>();
        for (int i = 0; i < timeSlots; i++) {
            int sumCol = 0;
            for (int j = 0; j < plugs; j++) {
                if (Xbest[j][i] != null) {
                    sumCol += Xbest[j][i].getValueCharged();
                }
            }
            valueChargedByCars.add(sumCol);
        }
        this.printEdiffOldAndNewGraphic(ediffList, ediffNew, valueChargedByCars);
        return Xbest;
    }

    private void printFitnessAndSolutionsInFile(ArrayList<Double> bestFitnessAmongEachIteration) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet spreadsheet = workbook.createSheet("FitnessValues");
        XSSFRow row;
        for (int i = 0; i < bestFitnessAmongEachIteration.size(); i++) {
            row = spreadsheet.createRow(i);
            Cell cell = row.createCell(0);
            cell.setCellValue(bestFitnessAmongEachIteration.get(i));

        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd_M_yyyy_hh_mm_ss");

        FileOutputStream out = new FileOutputStream(
                new File("FitnessValue" + sdf.format(new Date()) + ".xlsx"));

        workbook.write(out);
        out.close();
    }

    private void printEdiffOldAndNewGraphic(ArrayList<Double> ediffOld, ArrayList<Double> ediffNew, ArrayList<Integer> valueChargedByCars) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet spreadsheet = workbook.createSheet("EdiffValues");
        XSSFRow row;
        row = spreadsheet.createRow(0);
        Cell cell = row.createCell(0);
        cell.setCellValue("Old ediff values");
        Cell cell2 = row.createCell(1);
        cell2.setCellValue("New ediff values");
        Cell cell3 = row.createCell(2);
        cell3.setCellValue("Value charged in cars");
        for (int i = 0; i < ediffOld.size(); i++) {
            row = spreadsheet.createRow(i + 1);
            Cell cellOld = row.createCell(0);
            Cell cellNew = row.createCell(1);
            Cell cellCar = row.createCell(2);
            cellOld.setCellValue(ediffOld.get(i));
            cellNew.setCellValue(ediffNew.get(i));
            cellCar.setCellValue(valueChargedByCars.get(i));
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd_M_yyyy_hh_mm_ss");

        FileOutputStream out = new FileOutputStream(
                new File("EdiffValues" + sdf.format(new Date()) + ".xlsx"));

        workbook.write(out);
        out.close();

    }

    private ArrayList<Double> calculateNewEdiff(ElectricVehicleChargedValue[][] solution, ArrayList<Double> ediffOld, Integer plugs, Integer timeSlots) {
        ArrayList<Double> newEdiff = new ArrayList<>();
        for (int i = 0; i < timeSlots; i++) {
            double sumCol = 0;
            for (int j = 0; j < plugs; j++) {
                if (solution[j][i] != null) {
                    sumCol += solution[j][i].getValueCharged();
                }
            }
            sumCol = ediffOld.get(i) - sumCol;
            newEdiff.add(sumCol);
        }
        return newEdiff;
    }

    private void printEuclideanDistance(ArrayList<Double> euclideanDistanceForPopulationEachIteration) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet spreadsheet = workbook.createSheet("EuclideanDistance");
        XSSFRow row;
        for (int i = 0; i < euclideanDistanceForPopulationEachIteration.size(); i++) {
            row = spreadsheet.createRow(i);
            Cell cell = row.createCell(0);
            cell.setCellValue(euclideanDistanceForPopulationEachIteration.get(i));

        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd_M_yyyy_hh_mm_ss");

        FileOutputStream out = new FileOutputStream(
                new File("EuclideanDistance" + sdf.format(new Date()) + ".xlsx"));

        workbook.write(out);
        out.close();
    }
}

