import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hr.irb.*;
public class test {
    private String fileName="/home/ezop/radni/irb/doktorat/dokumentaija2/clanci/reference_doktorat/bono/sdf/EPI_SDF_Data/EPI_Kowwin_Data_SDF.sdf";
    @Test
    public  void testReadSDF() throws Exception {
        Sdf2Csv converter = new Sdf2Csv(this.fileName);
        converter.extractData();

    }
    @Test
    public  void testGetMoleculeData() throws Exception {
        Sdf2Csv converter = new Sdf2Csv(this.fileName);
        converter.extractData();
        IAtomContainer molecule1 = converter.getMolecules().get(0);
        Molecule mol =  converter.getMoleculeData(molecule1);
        Assert.assertNotNull(mol);
    }

    @Test
    public  void testWriteHashMapToCSV() throws Exception {
        List<Map<String,String>> list= new ArrayList<>();
        Map<String,String> map1 = new HashMap<String, String>(){};
        Map<String,String> map2 = new HashMap<String, String>(){};
        for (Integer i=1; i<10; i++){
            map1.put(i.toString(),i.toString());
            map2.put(i.toString(),"a"+i.toString());
        }
        list.add(map1);
        list.add(map2);

        List<String> keys = new ArrayList<>(ExportHelper.getKeysFromList(list));
        ExportHelper.writeHashMapToCsv(list, keys ,"/tmp/test.csv");
    }

    @Test
    public  void testWriteSDF2csv() throws Exception {
        Sdf2Csv converter = new Sdf2Csv(this.fileName);
        converter.extractData();

//        List<String> keys = new ArrayList<>(ExportHelper.getKeysFromList(list));
//        ExportHelper.writeHashMapToCsv(list, keys ,"/tmp/test.csv");
    }
}
