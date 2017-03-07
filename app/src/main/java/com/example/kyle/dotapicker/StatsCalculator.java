package com.example.kyle.dotapicker;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Kyle on 2/12/2017.
 */

public class StatsCalculator {

    int hero_pool_size = 115;

    public double[][] getHeroMap(Map<Integer,List<Integer>> map, Integer hero_id){
        Double percent;
        Integer[] exclude = {24};
        HashMap<Integer, Double> heroMap = new HashMap<Integer, Double>();

        for(int i = 1; i < hero_pool_size; i++){
            if(!Arrays.asList(exclude).contains(i)){

                List<Integer> winLoss = map.get(i);

                if(winLoss.get(0)+winLoss.get(1) == 0){
                    percent = Double.valueOf(0);
                }else{
                    percent = Double.valueOf(winLoss.get(0))/Double.valueOf((winLoss.get(0) + winLoss.get(1)));
                }

                heroMap.put(i,percent);
            }
        }
        double[][] hero_array = mapToList(heroMap, hero_id);

        return hero_array;
    }

    public double[][] mapToList(Map<Integer, Double> map, Integer hero_id){
        Integer[] exclude = {24};
        double[][] hero_array = new double[hero_pool_size][hero_pool_size];
        for(int i = 1; i < hero_pool_size; i++){
            if(!Arrays.asList(exclude).contains(i)){
                hero_array[i][hero_id] = map.get(i);
            }
        }
        return hero_array;
    }

    public double[][] getHeroWinMap(Map<Integer,List<Integer>> map, Integer hero_id){
        Integer[] exclude = {24};
        HashMap<Integer, Double> heroMap = new HashMap<Integer, Double>();
        for(int i = 1; i < hero_pool_size; i++){
            if(!Arrays.asList(exclude).contains(i)){
                List<Integer> winLoss = map.get(i);
                heroMap.put(i, Double.valueOf(winLoss.get(0)));
            }
        }
        double[][] hero_array = mapToList(heroMap, hero_id);

        return hero_array;
    }

    public double[][] getHeroLossMap(Map<Integer,List<Integer>> map, Integer hero_id){
        Integer[] exclude = {24};
        HashMap<Integer, Double> heroMap = new HashMap<Integer, Double>();
        for(int i = 1; i < hero_pool_size; i++){
            if(!Arrays.asList(exclude).contains(i)){
                List<Integer> winLoss = map.get(i);
                heroMap.put(i, Double.valueOf(winLoss.get(1)));
            }
        }
        double[][] hero_array = mapToList(heroMap, hero_id);

        return hero_array;
    }

}
