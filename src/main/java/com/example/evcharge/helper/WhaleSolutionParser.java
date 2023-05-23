package com.example.evcharge.helper;

import com.example.evcharge.models.ElectricVehicleChargedValue;
import org.javatuples.Pair;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WhaleSolutionParser {

    public List<Pair<ElectricVehicleChargedValue,Integer>> parseSolution(ElectricVehicleChargedValue[][] solution){
        List<Pair<ElectricVehicleChargedValue,Integer>> transformedSolution = new ArrayList<>();
        int row = solution.length;
        int col = solution[0].length;

        for(int j=0;j<col;j++){
            for(int i=0;i<row;i++){
                if(solution[i][j]!=null){
                    transformedSolution.add(Pair.with(solution[i][j],j));
                }
            }
        }
        return transformedSolution;
    }
}
