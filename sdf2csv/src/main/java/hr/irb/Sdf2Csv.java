package hr.irb;

import com.github.jferard.fastods.*;
import org.apache.commons.io.FileUtils;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.iterator.IteratingSDFReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Array;
import java.util.*;
import java.util.logging.Logger;

public class Sdf2Csv {
    private String fileName;
    private IteratingSDFReader reader;
    private List<IAtomContainer> molecules= new ArrayList<IAtomContainer>();
    private List<Molecule> parsedMoleculeData = new ArrayList<Molecule>();
    private HashSet<String> propNames  = new HashSet<String>();
    public Sdf2Csv(String fileName) throws Exception {
        this.fileName=fileName;
        if (!new File(fileName).exists()) throw new Exception("File not exists");
        this.readSDF();
        this.extractData();

    }
    public static void main(String[] args) throws Exception {
        if (args.length ==0) {
            ResourceHelper.printHelp();
            return;
        }
        String fileName = args[0];
        if (!new File(fileName).exists()){
            ResourceHelper.printHelp();
            return;
        }
        Sdf2Csv converter = new Sdf2Csv(fileName);
        converter.extractData();
    }
    public void extractData() throws Exception {
        molecules = this.getMolecules();
        parseMolecules();
        setPropertyNames();
        writeMetadata2Csv();
        writeCoordinatesToCsv(true);
        writeCoordinatesToCsv(false);
        writeMetadata2ODS();
        writeCoordinatesToODS(true);
        writeCoordinatesToODS(false);
    }

    private String getFileName(boolean isMetadata){
        return this.fileName+ ((isMetadata)? ".csv":".coord.csv");
    }

    public void writeMetadata2Csv(   ) throws Exception {
        String outFile = getFileName(true);
        StringBuilder output = new StringBuilder();

//        output.append("title").append("\n");
        for (String key : propNames){
            output.append(key).append("\t");
        }
        output.append("\n");

        for (Molecule map: parsedMoleculeData) {
            for (String key : propNames){
                String val = map.properties.get(key);
                val = (val == null)?"":val;
                output.append(val);
                output.append("\t");
            }
            output.append("\n");
        }
//        output.append()
        FileUtils.writeStringToFile(new File(outFile), output.toString());
    }


    public void writeMetadata2ODS(   ) throws Exception {
        final OdsFactory odsFactory = OdsFactory.create(Logger.getLogger("SDF-export"), Locale.US);
        final AnonymousOdsFileWriter writer = odsFactory.createWriter();
        final OdsDocument document = writer.document();
        final Table table = document.addTable("SDF-export");
        final TableRowImpl row = table.getRow(0);

        String outFile = this.fileName+".ods";

//        output.append("title").append("\n");
        int colNdx= 0;
        for (String key : propNames) {
            final TableCell cell = row.getOrCreateCell(colNdx);
            cell.setStringValue(key);
            colNdx++;
        }

        int rowNdx = 1;
        for (Molecule mol: parsedMoleculeData) {
            final TableRowImpl row1 = table.getRow(rowNdx);
            colNdx =0;
            for (String key : propNames){
                String val = mol.properties.get(key);
                val = (val == null)?"":val;
                final TableCell cell = row1.getOrCreateCell(colNdx);
                cell.setStringValue(val);
                colNdx++;
            }
            rowNdx++;
        }
        writer.saveAs(new File( outFile));
    }

    private void writeCoordinatesToCsv(boolean is3D){
//TODO
    }
    private void writeCoordinatesToODS(boolean is3D){
//TODO
    }

    public void setPropertyNames(){
        this.propNames.clear();
        for (IAtomContainer molecule : molecules){
            Map<Object,Object> props =  molecule.getProperties();
            for (Map.Entry<Object,Object> entry: props.entrySet()){
                String key = (String)entry.getKey();
//                Object val = entry.getValue();
                if (!this.propNames.contains(key))
                    propNames.add(key);
            }
        }
    }
    public List<Molecule> getParsedMoleculeData() {
        return parsedMoleculeData;
    }



    public void parseMolecules(){
        this.parsedMoleculeData.clear();
        for (IAtomContainer molecule : molecules){
            Molecule mol = getMoleculeData(molecule);
            this.parsedMoleculeData.add(mol);
        }
    }

    public Molecule getMoleculeData(IAtomContainer mol){
        Molecule molData = new Molecule();
        molData.title = mol.getTitle();
        molData.id = mol.getID();
        molData.properties = new HashMap<String, String>();
        for (Object key : mol.getProperties().keySet()){
            String val = (String)mol.getProperties().get(key);
            molData.properties.put((String)key, val);
        }
        return molData;
    }

    private void readSDF() throws FileNotFoundException {
        File sdfFile = new File(this.fileName);
        IteratingSDFReader reader = new IteratingSDFReader(
                new FileInputStream(sdfFile), DefaultChemObjectBuilder.getInstance()
        );
        this.reader = reader;
    }



    public List<IAtomContainer> getMolecules(){
        while (reader.hasNext()) {
            IAtomContainer molecule = (IAtomContainer)reader.next();
            molecules.add(molecule);
        }
        return molecules;
    }


}
