package hr.irb;

import com.google.gson.Gson;
import hr.irb.formulator.Formula;
import hr.irb.helpers.MolSdfHelper;
import hr.irb.helpers.ResourceHelper;
import hr.irb.results.CalculationResults;
import org.openscience.cdk.interfaces.IAtomContainer;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

import static java.lang.Thread.sleep;

public class ReformedIndex {

    public static void main(String[] args) throws Exception {
        if (args.length<1){
            printHelp();
            return;
        }

        String format = "TEXT";
        String inputFileName = "";
        String formulas = "";
        boolean skipHydrogens = false;
        boolean sumMultipleBondsAsManyBonds = false;
        for (int i=0; i<args.length ; i++){
            if (args[i].equalsIgnoreCase("-i")) inputFileName = args[i+1];
            if (args[i].equalsIgnoreCase("-format")) format = args[i+1];
            if (args[i].equalsIgnoreCase("-h")) skipHydrogens = true;
            if (args[i].equalsIgnoreCase("-formulas")) formulas = args[i+1];
            if (args[i].equalsIgnoreCase("-m")) sumMultipleBondsAsManyBonds = true;
        }

        if (format.equalsIgnoreCase("json")) format = "JSON";
        if (format.equalsIgnoreCase("csv")) format = "CSV";
        if (!(new File(inputFileName)).exists()) throw new Exception("File "+inputFileName+" doesn't exist!");

        List<IAtomContainer> molecules = MolSdfHelper.readMolSdfFromFile(inputFileName);
        for (IAtomContainer container : molecules){
            MolSdfHelper.setOrderedIdsToAtoms(container);
        }
        //TODO ubacit atomima ID-ove
        List<CalculationResults> results = doCalculations(molecules,skipHydrogens,sumMultipleBondsAsManyBonds,formulas);
        if (!format.equalsIgnoreCase("JSON"))
            printSettings(format,skipHydrogens,sumMultipleBondsAsManyBonds,formulas);
        print(format, results);
    }

    private static void printSettings(String format, boolean skipHydrogens, boolean sumMultipleBondsInFrequenciesAsManyBonds, String formulas){
        System.out.println("#############################################");
        System.out.println("################SETINGS######################");
        System.out.println("#Include hydrogens in frequencies: "+((skipHydrogens)?"FALSE":"TRUE"));
        System.out.println("#Sum multiple bonds as single: "+((sumMultipleBondsInFrequenciesAsManyBonds)?"FALSE":"TRUE"));

        String form = formulas.replace(" ","");
        System.out.println("#Formulas: "+ (form.equalsIgnoreCase("")? "" :form));
        System.out.println("#############################################");
    }

    private static void print(String format, List<CalculationResults> results){
        if (format.equalsIgnoreCase("json")) {
            Gson gson = new Gson();
            System.out.print(gson.toJson(results));
        }else if (format.equalsIgnoreCase("csv")){
            System.out.print("\n");
            System.out.print("#\t");
            System.out.print("SMILES\t");
            for (int i = 1; i<=7 ; i++)
                for (int j = i; j <= 7; j++)
                    System.out.print(String.format("[%s, %s]\t",i,j));


            //System.out.print("HARMONIC_WITH_ORDER\tHI_S_WITH_ORDER\tHI_P_WITH_ORDER\tHARMONIC\tHI_S\tHI_P\n");
            Set<String> indexi = results.get(0).calculations.keySet();

            ArrayList<String> indexiSorted = new ArrayList<String>();

            for (String key: indexi )
                indexiSorted.add(key);
            Collections.sort(indexiSorted);

            for (String key : indexiSorted) {
                System.out.print(key.toString()+ "\t");
            }


            System.out.print("\n");
            int x = 0;
            for (CalculationResults res : results){
                x++;
                System.out.print(Integer.toString(x)+"\t");
                System.out.print(res.uniqueSmiles+"\t");
                for (int i = 1; i<=7 ; i++) {
                    for (int j = i; j <= 7; j++) {
                        String key = String.format("%s,%s", Integer.toString(i),Integer.toString(j));
                        System.out.print(res.valencePairsAndFrequences.get(key).intValue() + "\t");
                    }
                }
                for (String key : indexiSorted)
                    System.out.print(res.calculations.get(key)+"\t");

                System.out.print("\n");
            }

            System.out.print("\n\n");
            System.out.print("\t");
//            for (int i = 1; i<=7 ; i++)
//                for (int j = i; j <= 7; j++)
//                    System.out.print(String.format("[%s, %s]\t",i,j));

            System.out.print("\n");
        } else{
            for (CalculationResults res : results){

                System.out.println("#############################################");
                System.out.println("Title:\t" + res.moleculeName);
                System.out.println("Unique smiles:\t" + res.uniqueSmiles);
                System.out.println("User smiles:\t"+res.userSmiles);

                System.out.println("\n");
                System.out.println("###############CALCULATIONS##################");
                for (String key : res.calculations.keySet()){
                    System.out.println(key.toString()+":\t"+res.calculations.get(key));
                }

                System.out.println("\n");
                System.out.println("###############FREQUENCIES##################");
                System.out.println("Valence1\tValence2\tCount");
                for (int i = 1; i<=7 ; i++){
                    for (int j = i; j<=7 ; j++){
                        String key = String.format("%s,%s", Integer.toString(i),Integer.toString(j));

                        System.out.print(key.replace(",","\t"));
                        System.out.print("\t");
                        System.out.println(res.valencePairsAndFrequences.get(key).intValue());
                    }
                }

//                System.out.println("\n");
//                System.out.println("###############F pairs##################");
//                System.out.println("Valence1\tValence2\tCount");
//                for (int i = 1; i<=7 ; i++){
//                    for (int j = i; j<=7 ; j++){
//                        String key = String.format("%s,%s", Integer.toString(i),Integer.toString(j));
//
//                        System.out.print(key.replace(",","\t"));
//                        System.out.print("\t");
//                        System.out.println(res.FPairs.get(key).intValue());
//                    }
//                }



            }

        }
    }

    private static List<CalculationResults> doCalculations(final List<IAtomContainer> molecules, boolean skipHydrogen, boolean addMultipleBondsAsMany, String formulas) throws Exception {
        List<Formula> formulaList = Formula.stringToFormulasList(formulas);
        List<CalculationResults> results = new ArrayList<CalculationResults>();
        for (IAtomContainer molecule : molecules) {
            ChemicalIndexes ndx = new ChemicalIndexes(molecule,skipHydrogen,addMultipleBondsAsMany,formulaList);

            CalculationResults res = new CalculationResults();
            res.calculations = ndx.getIndexes();
//            res.FPairs = ndx.getFs(skipHydrogen,addMultipleBondsAsMany);
            res.valencePairsAndFrequences = ndx.getFrequencies(skipHydrogen, addMultipleBondsAsMany);
            res.moleculeName = ndx.getTitle();
            res.uniqueSmiles = ndx.getUniqueSmiles();
            res.userSmiles   = ndx.getUserSmiles();
            res.paramSkipHydrogens = skipHydrogen;
            res.paramSumMultipleBondAsSingle = !addMultipleBondsAsMany;
            results.add(res);
        }
        return results;
    }


    private static void printHelp() throws Exception {
        List<String> help = ResourceHelper.resourceToList("/help.txt");
        for (String line : help) {
            System.out.println(line);
        }
    }



}
