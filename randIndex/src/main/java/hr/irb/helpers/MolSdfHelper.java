package hr.irb.helpers;

import hr.irb.formulator.ArgVector;
import hr.irb.formulator.Calculator;
import hr.irb.formulator.Formula;
import org._3pq.jgrapht.Edge;
import org._3pq.jgrapht.edge.UndirectedEdge;
import org._3pq.jgrapht.graph.SimpleGraph;
import org.jgrapht.*;
//import org.jgrapht.graph.SimpleGraph;
import org.openscience.cdk.Atom;
import org.openscience.cdk.Bond;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.graph.BFSShortestPath;
//import org.jgrapht.alg.shortestpath.AStarShortestPath;

import org.openscience.cdk.graph.BFSShortestPath;
import org.openscience.cdk.graph.rebond.Bspt;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.io.iterator.IteratingSDFReader;

import java.io.FileInputStream;
import java.util.*;

/**
 * Created by x on 6/7/2014.
 */
public class MolSdfHelper {
    public static List<IAtomContainer> readMolSdfFromFile(String fullPathToSDF) throws Exception {
        List<IAtomContainer> moleculeList = new ArrayList<IAtomContainer>();
        IteratingSDFReader reader = new IteratingSDFReader(
            new FileInputStream(fullPathToSDF)
          , DefaultChemObjectBuilder.getInstance()
        );
        while (reader.hasNext()) {
            IAtomContainer container = (IAtomContainer) reader.next();
            moleculeList.add(container);
        }
        return moleculeList;
    }
    public static void setOrderedIdsToAtoms(IAtomContainer container){
        int i = 0;
        for (IAtom atom : container.atoms()){
            atom.setID(Integer.toString(i++));
        }
    }
//    public static Ha<String>
    public static int getNumberOfConnections(boolean isFirstAtom, boolean skipHydrogen,IAtom atom, IAtomContainer container){
        int bondCount = 0;

        Iterator<IBond> iterator = container.bonds().iterator();

        while (iterator.hasNext()){
            Bond bond = (Bond)iterator.next();
            IAtom atom1 = bond.getAtom(0);
            IAtom atom2 = bond.getAtom(1);
            if ((isFirstAtom && atom1.hashCode()==atom.hashCode()) ||  (!isFirstAtom && atom2.hashCode()==atom.hashCode() ))
            {
                if (skipHydrogen && (atom1.getSymbol().trim().equalsIgnoreCase("H") || atom2.getSymbol().trim().equalsIgnoreCase("H"))) bondCount--;
                bondCount++;
            }
        }

        return bondCount;
    }
    public static int getNumberOfConnections(IAtom atom, IAtomContainer container, HashSet<String> skipAtoms, boolean addMultipleBondsAsMany ){
        int bondCount = 0;
        Iterator<IBond> iterator = container.bonds().iterator();

        while (iterator.hasNext()){
            Bond bond = (Bond)iterator.next();
            IAtom atom1 = bond.getAtom(0);
            IAtom atom2 = bond.getAtom(1);
            if ((atom1.hashCode()==atom.hashCode()) || (atom2.hashCode()==atom.hashCode() ))
            {
                if (skipAtoms != null && skipAtoms.size()>0 && (skipAtoms.contains(atom1.getSymbol().trim()) || skipAtoms.contains(atom2.getSymbol().trim()))) continue;
                if (addMultipleBondsAsMany){
                    if (bond.getOrder().equals(IBond.Order.SINGLE)) {
                        bondCount++;
                    }else if (bond.getOrder().equals(IBond.Order.DOUBLE)) {
                        bondCount+=2;
                    }else if (bond.getOrder().equals(IBond.Order.TRIPLE)) {
                        bondCount+=3;
                    }else if (bond.getOrder().equals(IBond.Order.QUADRUPLE)) {
                        bondCount+=4;
                    }else{
                        bondCount++;
                    }
                }   else {
                    bondCount++;
                }

            }
        }

        return bondCount;
    }
    public  static List<String> set2orderedList(Set<String> set){
        ArrayList<String> list = new ArrayList<String>();
        for (String key: set )
            list.add(key);
        Collections.sort(list);
        return list;
    }
    public static Double getNumberOfConnectionsDbl(IAtom atom, IAtomContainer container, boolean skipHydrogen, boolean addMultipleBondsAsMany ){
        return new Double(getNumberOfConnections( atom,  container,  skipHydrogen,  addMultipleBondsAsMany ));
    }

    public static int getNumberOfConnections(IAtom atom, IAtomContainer container, boolean skipHydrogen, boolean addMultipleBondsAsMany ){
        int bondCount = 0;

        Iterator<IBond> iterator = container.bonds().iterator();

        while (iterator.hasNext()){
            Bond bond = (Bond)iterator.next();
            IAtom atom1 = bond.getAtom(0);
            IAtom atom2 = bond.getAtom(1);
            if ((atom1.hashCode()==atom.hashCode()) || (atom2.hashCode()==atom.hashCode() ))
            {
                if (skipHydrogen && (atom1.getSymbol().trim().equalsIgnoreCase("H") || atom2.getSymbol().trim().equalsIgnoreCase("H"))) continue;
                if (addMultipleBondsAsMany){
                    if (bond.getOrder().equals(IBond.Order.SINGLE)) {
                        bondCount++;
                    }else if (bond.getOrder().equals(IBond.Order.DOUBLE)) {
                        bondCount+=2;
                    }else if (bond.getOrder().equals(IBond.Order.TRIPLE)) {
                        bondCount+=3;
                    }else if (bond.getOrder().equals(IBond.Order.QUADRUPLE)) {
                        bondCount+=4;
                    }else{
                        bondCount++;
                    }
                }   else {
                    bondCount++;
                }

            }
        }

        return bondCount;
    }
    public static int getNumberOfConnections(IAtom atom, IAtomContainer container, boolean skipHydrogen){
        int bondCount = 0;

        Iterator<IBond> iterator = container.bonds().iterator();

        while (iterator.hasNext()){
            Bond bond = (Bond)iterator.next();
            IAtom atom1 = bond.getAtom(0);
            IAtom atom2 = bond.getAtom(1);
            if ((atom1.hashCode()==atom.hashCode()) || (atom2.hashCode()==atom.hashCode() ))
            {
                if (skipHydrogen && (atom1.getSymbol().trim().equalsIgnoreCase("H") || atom2.getSymbol().trim().equalsIgnoreCase("H"))) bondCount--;
                bondCount++;
            }
        }

        return bondCount;
    }


    public static boolean bondExists(IAtom atom1,IAtom atom2, IAtomContainer container){
        Iterator<IBond> iterator = container.bonds().iterator();
        while (iterator.hasNext()){
            Bond bond = (Bond)iterator.next();
            if ((bond.getAtom(0).hashCode()==atom1.hashCode()) && (bond.getAtom(1).hashCode()==atom2.hashCode() )
                || (bond.getAtom(1).hashCode()==atom1.hashCode()) && (bond.getAtom(0).hashCode()==atom2.hashCode()) )
            {
                return true;
            }
        }

        return false;
    }
    public static Double sumHashMap(HashMap<String,Double> map){
        Double sum = new Double(0);
        for (Double value : map.values()) {
            sum += value;
        }
        return  sum;
    }
    private static void fillEmptyFrequencies(HashMap<String,Integer> map){
        for (int i = 1; i<=7 ; i++){
            for (int j = i; j<=7 ; j++){
                String key = String.format("%s,%s",Integer.toString(i),Integer.toString(j));
                map.put(key,0);
            }
        }
    }
    public  static  List<IBond> iterableBonds2bondList(Iterable<IBond> bonds){
        List<IBond> bondList = new ArrayList<IBond>();
        Iterator<IBond> iterator = bonds.iterator();
        while (iterator.hasNext()) {
            bondList.add((Bond) iterator.next());
        }
        return bondList;
    }

//    public  static void test(){
//        //org.openscience.cdk.graph
//        IAtomContainer molecule;
//        molecule.gr
//        BFSShortestPath b = BFSShortestPath.findPathBetween();
//
//    }

    public static boolean bondListHasAtoms(List<IBond> bonds, HashSet<String> atoms) throws Exception {
        if (atoms == null || atoms.size()==0) return false;
        for (IBond bond : bonds){
            if (atoms.contains(bond.getAtom(0).getSymbol().trim()) || atoms.contains(bond.getAtom(1).getSymbol().trim())) return  true;
        }
        return false;
    }

    public  static  List<IBond> getShortestPath(IAtomContainer molecule,IAtom atom1,IAtom atom2) throws Exception {
        if (molecule == null || molecule.getAtomCount()==0)
            throw new Exception("Molecule is empty");

        List<IBond> bondsPath = new ArrayList<IBond>();
//        org.jgrapht.graph.SimpleGraph graph = org.openscience.cdk.graph.MoleculeGraphs.getMoleculeGraph(molecule);
        SimpleGraph graph = org.openscience.cdk.graph.MoleculeGraphs.getMoleculeGraph(molecule);

//        AStarShortestPath path = new AStarShortestPath();

        List<Edge> path = BFSShortestPath.findPathBetween(graph, atom1, atom2);

        Iterator iterator = path.iterator();
        while (iterator.hasNext()) {
            UndirectedEdge edge = (UndirectedEdge)iterator.next();
            IAtom atomA = (IAtom)edge.getSource();
            IAtom atomB = (IAtom)edge.getTarget();

            Bond bond = (Bond) molecule.getBond(atomA, atomB);
            bondsPath.add(bond);
        }

        return bondsPath;
    }

    public static List<IBond> filterBondsBySymbol(List<IBond> srcBonds, HashSet<String> skippingAtoms){
        if (skippingAtoms == null || skippingAtoms.size()==0) return srcBonds;
        if (srcBonds == null || srcBonds.size()==0) return srcBonds;

        List<IBond> destBonds = new ArrayList<IBond>();

        Iterator<IBond> iterator = srcBonds.iterator();
        while (iterator.hasNext()){
            Bond bond = (Bond)iterator.next();
            Atom atom1 = (Atom)bond.getAtom(0);
            Atom atom2 = (Atom)bond.getAtom(1);

            if (skippingAtoms.contains(atom1.getSymbol().trim())) continue;
            if (skippingAtoms.contains(atom2.getSymbol().trim())) continue;

            destBonds.add(bond);
        }

        return destBonds;

    }

    /**
     * for each valence map it generates formula
     * @param valsAndFreqs
     * @param formula
     * @param order
     * @return
     * @throws Exception
     */
    public static HashMap<String,Double> formulizeValences(HashMap<String,Integer> valsAndFreqs,  Formula  formula, Boolean order) throws Exception {
        HashMap<String,Double> resMap = new HashMap<String,Double>();
        for (String key: valsAndFreqs.keySet()) {
            Integer val = valsAndFreqs.get(key);
            ArgVector vect = new ArgVector(key,new Double(val),true);
            Double res = Calculator.calculatePoint(vect,formula);
            resMap.put(key,res);
        }
        return resMap;
    }

    public static HashMap<String,Integer> getValenceFrequencies(IAtomContainer container, boolean skipHydrogen, boolean addMultipleBondsAsMany){
        HashMap<String,Integer> freq = new HashMap<String,Integer>();
        /*
            potrazi sve veze.
            iskljuci H veze
            za svaku vezu prebroji Ne-H ili Da-H veze
            okreni livu i desnu stranu
            spremi frekvencije
         */
        Iterator<IBond> iterator = container.bonds().iterator();

        fillEmptyFrequencies(freq);

        while (iterator.hasNext()){
            Bond bond = (Bond)iterator.next();
            IAtom atom1 = bond.getAtom(0);
            IAtom atom2 = bond.getAtom(1);
            if (skipHydrogen && (atom1.getSymbol().trim().equalsIgnoreCase("H") || atom2.getSymbol().trim().equalsIgnoreCase("H"))) continue;
            int numberOfConnectionsOfAtom1 = getNumberOfConnections(atom1,container,skipHydrogen,addMultipleBondsAsMany);
            int numberOfConnectionsOfAtom2 = getNumberOfConnections(atom2,container,skipHydrogen,addMultipleBondsAsMany);

            String key = "%s,%s";
            if (numberOfConnectionsOfAtom1<numberOfConnectionsOfAtom2) {
                key = String.format(key,Integer.toString(numberOfConnectionsOfAtom1), Integer.toString(numberOfConnectionsOfAtom2));
            } else {
                key = String.format(key,Integer.toString(numberOfConnectionsOfAtom2), Integer.toString(numberOfConnectionsOfAtom1));
            }

            if (!freq.containsKey(key)) freq.put(key,0);
            freq.put(key,freq.get(key)+1);
        }
        return freq;
    }
}
