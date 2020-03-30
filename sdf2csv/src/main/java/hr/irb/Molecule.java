package hr.irb;

import java.util.HashMap;

public class Molecule {
    public String title;
    public HashMap<String,String> properties = new HashMap<String, String>();
    public String id;

//    public String getTitle(){
//        return this.getMolecule().getProperty("cdk:Title").toString();
//    }
//
//    public String getUniqueSmiles(){
//        return this.getMolecule().getProperty("UNIQUE_SMILES").toString();
//    }
//    public String getUserSmiles(){
//        return this.getMolecule().getProperty("USER_SUPPLIED_SMILES").toString();
//    }
}
