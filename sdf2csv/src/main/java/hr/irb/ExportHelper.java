package hr.irb;

import com.github.jferard.fastods.*;
import org.apache.commons.io.FileUtils;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.File;
import java.io.StringWriter;
import java.util.*;
import java.util.logging.Logger;

public class ExportHelper {
    public static Set<String> getKeysFromList(List<Map<String,String>> list){
        Set<String> set = new HashSet<>();
        for(Map<String,String> map : list){
            //TODO ovdi moram sve kljuceve pokupit i nakrtcat u set
            set.addAll(map.keySet());
        }
        return set;
    }
    public static void writeHashMapToCsv(List<Map<String, String>> mapList, List<String> keys, String outFile) throws Exception {
        StringBuilder output = new StringBuilder();
        for (String key : keys){
            output.append(key);
            output.append("\t");
        }
        output.append("\n");

        for (Map<String, String> map: mapList) {
            for (String key : keys){
                output.append(map.get(key));
                output.append("\t");
            }
            output.append("\n");
        }
//        output.append()
        FileUtils.writeStringToFile(new File(outFile), output.toString());
    }


//    private static String hashMapToCsvLine(Map<String, String> map) throws Exception {
//        StringWriter output = new StringWriter();
//        try (ICsvListWriter listWriter = new CsvListWriter(output,
//                CsvPreference.STANDARD_PREFERENCE)){
//            for (Map.Entry<String, String> entry : map.entrySet()){
//                listWriter.write(entry.getKey(), entry.getValue());
//            }
//        }
////
//        return output.toString();
//    }

//TODO testirat
    public static void writeHashMapToODS(List<Map<String, String>> mapList, List<String> keys, String outFile) throws Exception {
        final OdsFactory odsFactory = OdsFactory.create(Logger.getLogger("Started writting ODS document"), Locale.US);
        final AnonymousOdsFileWriter writer = odsFactory.createWriter();
        final OdsDocument document = writer.document();
        final Table table = document.addTable("SDF-export");
        final TableRowImpl row = table.getRow(0);

        int colNdx= 0;
        for (String key : keys) {
            final TableCell cell = row.getOrCreateCell(colNdx);
            cell.setStringValue(key);
            colNdx++;
        }

        int rowNdx = 1;
        for (Map<String, String> map: mapList) {
            final TableRowImpl row1 = table.getRow(rowNdx);
            for (String key : keys){
                final TableCell cell = row1.getOrCreateCell(colNdx);
                cell.setStringValue(map.get(key));
            }
            rowNdx++;
        }

        writer.saveAs(new File("generated_files", outFile));
    }
}
