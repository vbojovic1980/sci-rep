package hr.irb;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ResourceHelper {


    public static List<String> resourceToList(String resourcePath) throws Exception{
        List<String> lines = new ArrayList<String>();
        final InputStream in  = ResourceHelper.class.getResourceAsStream(resourcePath);

//        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
//        final InputStream in =  classLoader.getResourceAsStream(resourcePath);
//        final InputStream in = ResourceHelper.class.getClass().getResourceAsStream(
//                resourcePath);
        final BufferedReader bufReader = new BufferedReader(
                new InputStreamReader(in));
        String line = "";
        while ((line = bufReader.readLine()) != null)
            lines.add(line);
        return lines;
    }

     public static HashMap<String,String> loadINIFile(String resourcePath, String delimiter) throws Exception {
        List<String> lines = resourceToList(resourcePath);
        HashMap<String,String> map = new HashMap<String,String>();
        for (String line : lines) {
            if (line.trim().equalsIgnoreCase("") || line.trim().indexOf(delimiter)==-1) continue;
            String key = line.split(delimiter)[0];
            String val = line.split(delimiter)[1];
            map.put(key,val);
        }
        return map;
     }
    public static void printHelp() throws Exception {
        List<String> help = ResourceHelper.resourceToList("/help.txt");
        for (String line : help) {
            System.out.println(line);
        }
    }
}
