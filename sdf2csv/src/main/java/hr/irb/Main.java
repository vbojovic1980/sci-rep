package hr.irb;

import java.io.File;

public class Main {

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
}
