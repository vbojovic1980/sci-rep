package hr.irb;

import com.google.common.collect.Lists;
import hr.irb.formulator.ArgVector;
import hr.irb.formulator.Calculator;
import hr.irb.formulator.Formula;
import hr.irb.helpers.MathHelper;
import hr.irb.helpers.MolSdfHelper;
import org.openscience.cdk.Atom;
import org.openscience.cdk.Bond;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

import java.util.*;

/**
 * Created by x on 6/7/2014.
 */

/*  uzmi sve veze, iskljuci ako je ric o H atomima
    prebroji konekcije

          */
public class ChemicalIndexes {
    private IAtomContainer molecule;
    private boolean skipHydrogens;
    private boolean addMultipleBondsAsMany;
    private HashSet<String> skippingAtoms = new HashSet<String>();
    private List<Formula> defaultFormulaList = new ArrayList<Formula>();
    private List<ArgVector> argList = new ArrayList<>();
    private List<Formula>  customFormulaList = new ArrayList<>();
    public ChemicalIndexes(IAtomContainer molecule,boolean skipHydrogens,boolean addMultipleBondsAsMany, List<Formula> formulaList) throws Exception {
        this.setAddMultipleBondsAsMany(addMultipleBondsAsMany);
        this.setSkipHydrogens(skipHydrogens);
        this.setMolecule(molecule);
        this.setCustomFormulaList(formulaList);
        if (this.isSkipHydrogens()) skippingAtoms.add("H");
        this.defaultFormulaList = Formula.loadFormulasFromINI("/formulae.ini");
        this.setCustomFormulaList(formulaList);
        this.argList = this.getFrequenciesAsArgs(this.isSkipHydrogens(),this.isAddMultipleBondsAsMany());
    }

    public IAtomContainer getMolecule() {
        return molecule;
    }
    public String getTitle(){
        try {
            return this.getMolecule().getProperty("cdk:Title").toString();
        } catch (Exception e){
            return "";
        }
    }

    public String getUniqueSmiles(){
        try {
            return this.getMolecule().getProperty("UNIQUE_SMILES").toString();
        } catch (Exception e){
            return null;
        }

    }
    public String getUserSmiles(){
        try{
            return this.getMolecule().getProperty("USER_SUPPLIED_SMILES").toString();
        } catch (Exception e){
            return "";
        }

    }
    public void setMolecule(IAtomContainer molecule) {
        this.molecule = molecule;
    }

    public HashMap<String,Double> getIndexes() throws Exception {
        HashMap<String,Double> map = new HashMap<String,Double>();
//        map.put("HI_P", getIndex_HI_P());
//        map.put("HI_S", getIndex_HI_S());
//        map.put("HARMONIC", getIndexHarmonic());
        map.put("F17", getIndex_HI_0_P()); //HI_P i HI_0S su ista stvar

//        map.put("M1", getIndex_Modified_Zagreb_M1());
//        map.put("M2", getIndex_Modified_Zagreb_M2());
        Map<String,Double> calculations = this.getCustomFormulasCalculated(this.getDefaultFormulaList(),this.argList);
        map.putAll(calculations);

        calculations = this.getCustomFormulasCalculated(this.getCustomFormulaList(),this.argList);
        map.putAll(calculations);

        this.fixInfinites(map);
        return map;
    }

    private void fixInfinites(HashMap<String,Double>  map){
        for(String key : map.keySet()){
            if (map.get(key).equals(Double.POSITIVE_INFINITY) || map.get(key).equals(Double.NEGATIVE_INFINITY)){
//                map.put(key,null);
                map.replace(key, null);
            }
        }
    }
    public Map<String,Double> getCustomFormulasCalculated(List<Formula> formulas, List<ArgVector> vectors) throws Exception {
        HashMap <String,Double> map = new HashMap<>();
        List<ArgVector> grouppedVector = ArgVector.sumarizeVector(vectors);
        for (Formula f : formulas){
            Calculator c = new Calculator(f,grouppedVector);
            List<Double> res =  c.getAll();
            Double sum = MathHelper.sumList(res);
            map.put(f.getName(), sum);
        }
        return map;
    }
    private List<ArgVector>  getFrequenciesAsArgs(boolean skipHydrogen, boolean addMultipleBondsAsMany){
        HashMap<String,Integer> freqsMap = this.getFrequencies(skipHydrogen,addMultipleBondsAsMany);
        List<ArgVector> freqList = new ArrayList<>();
        for (String key: freqsMap.keySet()) {
            Double keyLeft = Double.parseDouble(key.split(",")[0]);
            Double keyRight = Double.parseDouble(key.split(",")[1]);

            ArgVector vector = new ArgVector(keyLeft,keyRight,new Double(freqsMap.get(key)),true );
            freqList.add(vector);
        }
        return freqList;
    }


    private Double calculateCustom(Formula f) throws Exception {
        Double index = new Double(0);
        Iterator<IBond> iterator = molecule.bonds().iterator();
        while (iterator.hasNext()){
            Bond bond = (Bond)iterator.next();
            Atom atom1 = (Atom)bond.getAtom(0);
            Atom atom2 = (Atom)bond.getAtom(1);
            if (atom1.getSymbol().trim().equalsIgnoreCase("H") && this.isSkipHydrogens()) continue;
            if (atom2.getSymbol().trim().equalsIgnoreCase("H") && this.isSkipHydrogens()) continue;

            int atom1BondsCount = MolSdfHelper.getNumberOfConnections(atom1,this.getMolecule(),this.isSkipHydrogens(),this.isAddMultipleBondsAsMany());
            int atom2BondsCount = MolSdfHelper.getNumberOfConnections(atom2,this.getMolecule(),this.isSkipHydrogens(),this.isAddMultipleBondsAsMany());

            ArgVector args = new ArgVector();
            args.a = new Double(atom1BondsCount);
            args.b = new Double(atom2BondsCount);
            Double res =  Calculator.calculatePoint(args,f);
            if (res == null) continue;
            index += res;
        }
        return index;
    }




    public HashMap<String,Integer> getFrequencies(boolean skipHydrogen, boolean addMultipleBondsAsMany){
        return MolSdfHelper.getValenceFrequencies(this.getMolecule(),skipHydrogen,addMultipleBondsAsMany);
    }

    public HashMap<String,Double> getFs(boolean skipHydrogen, boolean addMultipleBondsAsMany){
        HashMap<String,Integer> freqs = MolSdfHelper.getValenceFrequencies(this.getMolecule(),skipHydrogen,addMultipleBondsAsMany);
        HashMap<String,Double> Fs = new HashMap<String, Double>();
        for( String key : freqs.keySet()){
            String keyLeft = key.split(",")[0];
            String keyRight = key.split(",")[1];
            Integer val = freqs.get(key);
            Double index = Double.parseDouble(keyLeft)*Double.parseDouble(keyLeft);
            index = Math.pow(index,-2);
            Fs.put(key,index);
        }
        return Fs;
    }



    /**
     * M1
     * @return
     */
    protected Double getIndex_F2(){
        Double index = new Double(0);
        Iterator<IBond> iterator = molecule.bonds().iterator();
        while (iterator.hasNext()){
            Bond bond = (Bond)iterator.next();
            Atom atom1 = (Atom)bond.getAtom(0);
            Atom atom2 = (Atom)bond.getAtom(1);
            if (atom1.getSymbol().trim().equalsIgnoreCase("H") && this.isSkipHydrogens()) continue;
            if (atom2.getSymbol().trim().equalsIgnoreCase("H") && this.isSkipHydrogens()) continue;

            int atom1BondsCount = MolSdfHelper.getNumberOfConnections(atom1,this.getMolecule(),this.isSkipHydrogens(),this.isAddMultipleBondsAsMany());
            int atom2BondsCount = MolSdfHelper.getNumberOfConnections(atom2,this.getMolecule(),this.isSkipHydrogens(),this.isAddMultipleBondsAsMany());

            index+=(((new Double(atom1BondsCount) +  (new Double(atom2BondsCount))))) ;
        }

        return index;
    }

    /**
     * M2
     * @return
     */
    protected Double getIndex_F3(){
        Double index = new Double(0);
        Iterator<IBond> iterator = molecule.bonds().iterator();
        while (iterator.hasNext()){
            Bond bond = (Bond)iterator.next();
            Atom atom1 = (Atom)bond.getAtom(0);
            Atom atom2 = (Atom)bond.getAtom(1);
            if (atom1.getSymbol().trim().equalsIgnoreCase("H") && this.isSkipHydrogens()) continue;
            if (atom2.getSymbol().trim().equalsIgnoreCase("H") && this.isSkipHydrogens()) continue;

            int atom1BondsCount = MolSdfHelper.getNumberOfConnections(atom1,this.getMolecule(),this.isSkipHydrogens(),this.isAddMultipleBondsAsMany());
            int atom2BondsCount = MolSdfHelper.getNumberOfConnections(atom2,this.getMolecule(),this.isSkipHydrogens(),this.isAddMultipleBondsAsMany());

            index+=(((new Double(atom1BondsCount) *  (new Double(atom2BondsCount))))) ;
        }

        return index;
    }

    protected Double getIndex_HI_P(){
        Double index = new Double(0);
        Iterator<IBond> iterator = molecule.bonds().iterator();
        while (iterator.hasNext()){
            Bond bond = (Bond)iterator.next();
            Atom atom1 = (Atom)bond.getAtom(0);
            Atom atom2 = (Atom)bond.getAtom(1);
            if (atom1.getSymbol().trim().equalsIgnoreCase("H") && this.isSkipHydrogens()) continue;
            if (atom2.getSymbol().trim().equalsIgnoreCase("H") && this.isSkipHydrogens()) continue;

            int atom1BondsCount = MolSdfHelper.getNumberOfConnections(atom1,this.getMolecule(),this.isSkipHydrogens(),this.isAddMultipleBondsAsMany());
            int atom2BondsCount = MolSdfHelper.getNumberOfConnections(atom2,this.getMolecule(),this.isSkipHydrogens(),this.isAddMultipleBondsAsMany());

            index+=(1/Math.sqrt((new Double(atom1BondsCount) *  (new Double(atom2BondsCount))))) ;
        }

        return index;
    }

    protected Double getIndex_HI_S(){
        Double index = new Double(0);
        Iterator<IBond> iterator = molecule.bonds().iterator();
        while (iterator.hasNext()){
            Bond bond = (Bond)iterator.next();
            Atom atom1 = (Atom)bond.getAtom(0);
            Atom atom2 = (Atom)bond.getAtom(1);
            if (atom1.getSymbol().trim().equalsIgnoreCase("H") && this.isSkipHydrogens()) continue;
            if (atom2.getSymbol().trim().equalsIgnoreCase("H") && this.isSkipHydrogens()) continue;

            int atom1BondsCount = MolSdfHelper.getNumberOfConnections(atom1,this.getMolecule(),this.isSkipHydrogens(),this.isAddMultipleBondsAsMany());
            int atom2BondsCount = MolSdfHelper.getNumberOfConnections(atom2,this.getMolecule(),this.isSkipHydrogens(),this.isAddMultipleBondsAsMany());

            index+=(1/Math.sqrt((new Double(atom1BondsCount) + (new Double(atom2BondsCount))))) ;
        }

        return index;
    }

    /**
     * modified zagreb index, excludes Hydrogen
     * @return
     */
    protected Double getIndex_Modified_Zagreb_M1(){
        Double index = new Double(0);
        Iterator<IAtom> atomIterator = molecule.atoms().iterator();
        while (atomIterator.hasNext()){
            Atom atom =  (Atom)atomIterator.next();
            if (atom.getSymbol().trim().equalsIgnoreCase("H") && this.isSkipHydrogens()) continue;
            int atomBondsCount = MolSdfHelper.getNumberOfConnections(atom,this.getMolecule(),this.isSkipHydrogens(),this.isAddMultipleBondsAsMany());
            index+= Math.pow(atomBondsCount,2);
        }
        return index;
    }

    /**
     * modified zagreb index, excludes Hydrogen
     * @return
     */
    protected Double getIndex_Modified_Zagreb_M2(){
        Double index = new Double(0);
        Iterator<IBond> iterator = molecule.bonds().iterator();
        while (iterator.hasNext()){
            Bond bond = (Bond)iterator.next();
            Atom atom1 = (Atom)bond.getAtom(0);
            Atom atom2 = (Atom)bond.getAtom(1);
            if (atom1.getSymbol().trim().equalsIgnoreCase("H") && this.isSkipHydrogens()) continue;
            if (atom2.getSymbol().trim().equalsIgnoreCase("H") && this.isSkipHydrogens()) continue;

            int atom1BondsCount = MolSdfHelper.getNumberOfConnections(atom1,this.getMolecule(),this.isSkipHydrogens(),this.isAddMultipleBondsAsMany());
            int atom2BondsCount = MolSdfHelper.getNumberOfConnections(atom2,this.getMolecule(),this.isSkipHydrogens(),this.isAddMultipleBondsAsMany());

            index+=
                    ((new Double(atom1BondsCount) * (new Double(atom2BondsCount))));
        }
        return index;
    }

    protected Double getIndexHarmonic(){
        Double index = new Double(0);
        Iterator<IBond> iterator = molecule.bonds().iterator();
        while (iterator.hasNext()){
            Bond bond = (Bond)iterator.next();
            Atom atom1 = (Atom)bond.getAtom(0);
            Atom atom2 = (Atom)bond.getAtom(1);
            if (atom1.getSymbol().trim().equalsIgnoreCase("H") && this.isSkipHydrogens()) continue;
            if (atom2.getSymbol().trim().equalsIgnoreCase("H") && this.isSkipHydrogens()) continue;

            int atom1BondsCount = MolSdfHelper.getNumberOfConnections(atom1,this.getMolecule(),this.isSkipHydrogens(),this.isAddMultipleBondsAsMany());
            int atom2BondsCount = MolSdfHelper.getNumberOfConnections(atom2,this.getMolecule(),this.isSkipHydrogens(),this.isAddMultipleBondsAsMany());

            index+=(
                 (new Double(atom1BondsCount) * (new Double(atom2BondsCount)))
               / (new Double(atom1BondsCount) + (new Double(atom2BondsCount)))
            ) ;
        }

        return index;
    }

    protected Double getIndex_HI_0_P(){
        Double index = new Double(0);
        Iterator<IAtom> atomIterator = molecule.atoms().iterator();
        while (atomIterator.hasNext()){
            Atom atom =  (Atom)atomIterator.next();
            if (atom.getSymbol().trim().equalsIgnoreCase("H") && this.isSkipHydrogens()) continue;
            int atomBondsCount = MolSdfHelper.getNumberOfConnections(atom,this.getMolecule(),this.isSkipHydrogens(),this.isAddMultipleBondsAsMany());
            index+= (1/Math.sqrt(new Double(atomBondsCount)));
        }
        return index;
    }
//    protected Double getIndex_HI_2(boolean isP){
//        Double index = new Double(0);
//
//        List<IBond> bonds = MolSdfHelper.iterableBonds2bondList(molecule.bonds());
//        bonds = MolSdfHelper.filterBondsBySymbol(bonds,this.getSkippingAtoms());
//
//        for (IBond bond : bonds){
//            Atom atom1a = (Atom)bond.getAtom(0);
//            Atom atom1b = (Atom)bond.getAtom(1);
//            int atom1BondsCount = MolSdfHelper.getNumberOfConnections(atom1a,this.getMolecule(),this.getSkippingAtoms(),this.isAddMultipleBondsAsMany());
//            int atom2BondsCount = MolSdfHelper.getNumberOfConnections(atom1b,this.getMolecule(),this.getSkippingAtoms(),this.isAddMultipleBondsAsMany());
//
//            List<IBond> bondsB = molecule.getConnectedBondsList(atom1b);
//            bondsB = MolSdfHelper.filterBondsBySymbol(bondsB,this.getSkippingAtoms());
//            if (bondsB == null || bondsB.size()==0) continue;
//            for (IBond bondB : bondsB){
//                Atom atom3 = ((Atom)bondB.getAtom(0));
//                Atom atom4 = ((Atom)bondB.getAtom(0));
//
//                int atom3BondsCount = MolSdfHelper.getNumberOfConnections(
//                        (atom3.getID().equalsIgnoreCase(atom1b.getID()))? atom4 : atom3
//                        ,this.getMolecule()
//                        ,this.getSkippingAtoms()
//                        ,this.isAddMultipleBondsAsMany());
//                if (isP) {
//                    index += 1/Math.sqrt(
//                        (new Double(atom1BondsCount))
//                      * (new Double(atom2BondsCount))
//                      * (new Double(atom3BondsCount))
//                    );
//                }else{
//                    index += 1/Math.sqrt(
//                        (new Double(atom1BondsCount))
//                        + (new Double(atom2BondsCount))
//                        + (new Double(atom3BondsCount))
//                    );
//                }
//            }
//
//
//        }
//
//        return index;
//    }
    private HashSet<IAtom> getAtomsBeforeBond(List<IBond> path, int position) throws Exception {
        if (path==null || position>path.size()) throw  new Exception("Empty path or position beyond path length");
        HashSet<IAtom> atoms = new HashSet<IAtom>();
        for (int i = 0 ; i< position ; i++){
            if (!atoms.contains(path.get(i).getAtom(0)))
                atoms.add(path.get(i).getAtom(0));
            if (!atoms.contains(path.get(i).getAtom(1)))
                atoms.add(path.get(i).getAtom(1));
        }
        return atoms;
    }

    private Double calculate(List<IBond> path,boolean isProduct) throws Exception {
        Double index = new Double(0);
        Double nazivnik = (isProduct)? new Double(1): new Double(0);
        for (int i = 0 ; i<path.size();i++){
//            HashSet<IAtom> atomsBefore = this.getAtomsBeforeBond(path,i);
            //ako imam atome ispred moram samo vidit tukaje li se
            IBond bond = path.get(i);

            IAtom atom1a = (IAtom)bond.getAtom(0);
            IAtom atom1b = (IAtom)bond.getAtom(1);
            int atom1aBondsCount = MolSdfHelper.getNumberOfConnections(atom1a,this.getMolecule(),this.getSkippingAtoms(),this.isAddMultipleBondsAsMany());
            int atom1bBondsCount = MolSdfHelper.getNumberOfConnections(atom1b,this.getMolecule(),this.getSkippingAtoms(),this.isAddMultipleBondsAsMany());
            if (i > 0){
                IAtom atom0a = (IAtom)((IBond)path.get(i)).getAtom(0);
                IAtom atom0b = (IAtom)((IBond)path.get(i)).getAtom(1);
                if (atom1a.getID().equalsIgnoreCase(atom0a.getID()) ||  atom1a.getID().equalsIgnoreCase(atom0b.getID())){
                    atom1aBondsCount = (isProduct) ? 1: 0;
                } else {
                    atom1bBondsCount = (isProduct) ? 1 : 0;
                }
            }
            nazivnik = (isProduct)
                    ? nazivnik * atom1aBondsCount * atom1bBondsCount
                    : nazivnik + atom1aBondsCount + atom1bBondsCount;
        }
        index = 1 / Math.sqrt(nazivnik);
        return index;
    }

    protected Double getIndex_HI_SP(boolean isP, int level) throws Exception {
        Double index = new Double(0);
        List<IAtom> atoms1 = Lists.newArrayList(getMolecule().atoms());
        List<IAtom> atoms2 = Lists.newArrayList(getMolecule().atoms());
        for (IAtom atom1 : atoms1){
//            System.out.print(atom1.getID());

            for (IAtom atom2 : atoms2){
                if (atom1.getID().compareTo(atom2.getID())>=0)    continue;
                List<IBond> path = MolSdfHelper.getShortestPath(getMolecule(),atom1,atom2);

                if (level >0 && path.size()!=level-1) continue;
                if (MolSdfHelper.bondListHasAtoms(path,this.getSkippingAtoms()))  continue;;

                index +=  this.calculate(path,isP);
            }
        }
        return index;
    }

//    protected Double getIndex_HI_2_P(){
//        Double index = new Double(0);
////TODO ovo nije gotovo jos
//        return null;
//    }

    public boolean isSkipHydrogens() {
        return skipHydrogens;
    }

    public void setSkipHydrogens(boolean skipHydrogens) {
        this.skipHydrogens = skipHydrogens;
    }


    public boolean isAddMultipleBondsAsMany() {
        return addMultipleBondsAsMany;
    }

    public void setAddMultipleBondsAsMany(boolean addMultipleBondsAsMany) {
        this.addMultipleBondsAsMany = addMultipleBondsAsMany;
    }

    public HashSet<String> getSkippingAtoms() {
        return skippingAtoms;
    }

    public void setSkippingAtoms(HashSet<String> skippingAtoms) {
        this.skippingAtoms = skippingAtoms;
    }



    public List<Formula> getCustomFormulaList() {
        return customFormulaList;
    }

    public void setCustomFormulaList(List<Formula> customFormulaList) {
        this.customFormulaList = customFormulaList;
    }

    public List<Formula> getDefaultFormulaList() {
        return defaultFormulaList;
    }

    public void setDefaultFormulaList(List<Formula> defaultFormulaList) {
        this.defaultFormulaList = defaultFormulaList;
    }
}
