import hr.irb.helpers.MolSdfHelper;
import hr.irb.ReformedIndex;
import org._3pq.jgrapht.Edge;
import org._3pq.jgrapht.graph.SimpleGraph;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.graph.BFSShortestPath;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.io.iterator.IteratingSDFReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by x on 6/6/2014.
 */
public class testReadMolSdf {

    //private String fileName = "Z:\\bono\\indeksi\\randIndex\\src\\main\\resources\\Alkani_39i60_SDF_SMILES_translator.sdf";

//    private String fileName = "/home/ezop/web/svastara/sci/bono/indeksi/randIndex/src/main/resources/Alkani_39i60_SDF_SMILES_translator.sdf";
    private String fileName = "/srv/radni/svastara/sci/bono/indeksi/randIndex/src/main/resources/Alkani_39i60_SDF_SMILES_translator.sdf";
    //private String fileName = "/home/ezop/web/svastara/sci/bono/indeksi/randIndex/src/main/resources/1testni.sdf";
    private List<IAtomContainer> molecules = new ArrayList<IAtomContainer>();
    @Before
    public void setUp() throws FileNotFoundException {
        /*
        File f = new File(fileName);
        if(!f.exists() || f.isDirectory()) {
            //triban resource nac..
        }
        */
        this.setMolecules();

    }
    private void setMolecules() throws FileNotFoundException {
        if (!new File(fileName).exists()) return;
        File sdfFile = new File(fileName);
        IteratingSDFReader reader = new IteratingSDFReader(
                new FileInputStream(sdfFile), DefaultChemObjectBuilder.getInstance()
        );
        while (reader.hasNext()) {
            IAtomContainer container = (IAtomContainer)reader.next();
            this.molecules.add(container);
        }
    }

//    @Test
    public void testRead() throws Exception {
        IAtomContainer molecule = this.molecules.get(5);
        IAtom atom1 = molecule.getAtom(0);
        IAtom atom2 = molecule.getAtom(15);
        List<IBond> path = MolSdfHelper.getShortestPath(molecule, atom1, atom2);
        Assert.assertNotNull("prazno je 15",path);

        for(IBond bond : path){
            System.out.println();
        }
    }

//    @Test
    public  void testFindConnection() throws Exception{
        //http://cdk.sourceforge.net/cdk-0.99.1/api/org/openscience/cdk/graph/MoleculeGraphs.html
        //http://cdk.sourceforge.net/cdk-0.99.1/api/org/openscience/cdk/graph/BFSShortestPath.html
        IAtomContainer molecule = this.molecules.get(5);
        IAtom atom1 = molecule.getAtom(0);
        IAtom atom2 = molecule.getAtom(1);

        SimpleGraph graph = org.openscience.cdk.graph.MoleculeGraphs.getMoleculeGraph(molecule);

        List<Edge> path = BFSShortestPath.findPathBetween(graph, atom1, atom2);
        Assert.assertNotNull("nije prazno",path);

        path = BFSShortestPath.findPathBetween(graph, atom1, molecule.getAtom(15));
        Assert.assertNotNull("prazno je 15",path);
    }
    //@Test
    public void testReformedIndexText() throws Exception {
        //String[] args = new String[]{"-m","-i",fileName,"-h"};
        String[] args = new String[]{"-m","-i",fileName,"-h"};
        ReformedIndex.main(args);
    }
//    @Test
//    public void testReformedIndexCSVMultiple() throws Exception {
//        String[] args = new String[]{"-format","CSV","-i",fileName,"-h"};
//        ReformedIndex.main(args);
//    }
    @Test
    public void testReformedIndexCSV() throws Exception {
//        String[] args = new String[]{"-m","-format","CSV","-i",fileName,"-h"};
        String[] args = new String[]{"-m","-format","CSV","-i",fileName,"-h", "-formulas","x+y,x-3y"};
        ReformedIndex.main(args);
    }
//    @Test
    public void testReformedIndexCSVNoFormula() throws Exception {
//        String[] args = new String[]{"-m","-format","CSV","-i",fileName,"-h"};
        String[] args = new String[]{"-m","-format","CSV","-i",fileName,"-h", "-formulas",""};
        ReformedIndex.main(args);
    }
//    @Test
    public void testReformedIndexJson() throws Exception {
        String[] args = new String[]{"-m","-format","JSON","-i",fileName,"-h","-formulas","x+y,x-3y"};
        ReformedIndex.main(args);
    }

    @Test
    public void testReformedIndexHelp() throws Exception {
//        String[] args = new String[]{"-m","-format","CSV","-i",fileName,"-h"};
        String[] args = new String[]{};
        ReformedIndex.main(args);
    }


    //@Test
    public void testReadMolSdf() throws FileNotFoundException {
        File sdfFile = new File(fileName);
        IteratingSDFReader reader = new IteratingSDFReader(
                new FileInputStream(sdfFile), DefaultChemObjectBuilder.getInstance()
        );
        while (reader.hasNext()) {
            IAtomContainer container = (IAtomContainer)reader.next();
            IBond bond = container.getBond(0);
            Atom atom1 = (Atom)bond.getAtom(0);
            Atom atom2 = (Atom)bond.getAtom(1);
            //System.out.print(((IAtom)container.atoms().iterator().next()).getID().toString());
            //IMolecule molecule = (IMolecule)reader.next();

            Assert.assertNotNull(atom1);
            Assert.assertNotNull(atom2);
            System.out.println(container.getProperty("cdk:Title").toString());
            System.out.print(1);
        }
        /*
        IteratingMDLReader reader = new IteratingMDLReader(
                new FileInputStream(sdfFile), DefaultChemObjectBuilder.getInstance()
        );

        */
    }
}
