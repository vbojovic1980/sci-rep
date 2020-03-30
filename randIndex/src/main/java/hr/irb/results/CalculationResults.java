package hr.irb.results;


import java.util.HashMap;
import java.util.Map;

/**
 * Created by x on 6/8/2014.
 */
public class CalculationResults {
    public String moleculeName;
    public String uniqueSmiles;
    public String userSmiles;
    public HashMap<String,Integer> valencePairsAndFrequences = new HashMap<String,Integer>();
    public HashMap<String,Double> calculations = new HashMap<String,Double>();
    public boolean paramSkipHydrogens;
    public boolean paramSumMultipleBondAsSingle;
}
