package hr.irb.formulator;

import hr.irb.helpers.ResourceHelper;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Formula {
    private String formula;
    private String name;


    public  Formula(final String formula, final String name) throws Exception {
        String cleanedFormula = this.cleanFormula(formula);
        if (cleanedFormula == null || cleanedFormula == "")
            throw new Exception("problem with formula");

        this.setFormula(cleanedFormula);
        this.setName((name == null)? formula : name);
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    private String cleanFormula(final String formula){
        String newFormula = formula;

        newFormula=newFormula.toLowerCase()
                .replace("log","log10")
                .replace("log1010","log10")
                .replace("ln","log")
                .replace(" ","")
                .replace("x","a")
                .replace("y","b");

        return newFormula;
    }

    public static List<Formula> stringToFormulasList(String formulas) throws Exception {
        List<Formula> formulasList = new ArrayList<Formula>();
        formulas = formulas
            .replace("\r\n",",")
            .replace("\n",",")
            .replace("\t",",");

        for (String formula: formulas.split(",")) {
            if (formula.equalsIgnoreCase("")) continue;
            Formula f = new Formula(formula,null);
            formulasList.add(f);
        }

        return  formulasList;
    }
    public static List<Formula> loadFormulasFromINI(final String formulasFile) throws Exception {
        List<Formula> formulas = new ArrayList<Formula>();
        HashMap<String,String> formulasList =  ResourceHelper.loadINIFile(formulasFile,";");
        for (String name: formulasList.keySet()) {
            String formulaVal = formulasList.get(name);
            Formula f = new Formula(formulaVal,name);
            formulas.add(f);
        }
        return  formulas;
    }
}
