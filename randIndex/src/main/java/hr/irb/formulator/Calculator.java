package hr.irb.formulator;

import com.sun.org.apache.xpath.internal.Arg;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Calculator {
    private List<ArgVector> inputData;
    private Formula formula;

    public Calculator(Formula formula, List<ArgVector> inputData) throws Exception {
        this.setInputData(inputData);
        this.setFormula(formula);
        checkAll();
    }


    public void Calculator(Formula f){
        this.setFormula(formula);
    }

    private void checkAll() throws Exception {
        if (this.getFormula() == null)
            throw new Exception("No formula given");
        if (this.getInputData() == null || this.getInputData().size()==0)
            throw new Exception("No input data given");
    }

    public List<Double> getAll() throws Exception {
        checkAll();
        List<Double> results = new ArrayList<Double>();
        for (ArgVector vect: this.getInputData()) {
            Double res = this.calculatePoint(vect);
            results.add(res);
        }

        return  results;
    }
    public static HashMap<Formula,List<Double>> calculateAll(List<Formula> formulas, List<ArgVector> inputData) throws Exception {
        HashMap<Formula,List<Double>> res = new HashMap<Formula,List<Double>>();

        for (Formula f: formulas) {
            Calculator c  = new Calculator(f, inputData);
            res.put(f , c.getAll());
        }

        return  res;
    }

    public static Double calculatePoint(ArgVector arg,Formula formula) throws Exception {
        try {
            Expression e = new ExpressionBuilder(formula.getFormula())
    //                .variables("a", "b" , "n")
                    .variables("a", "b" )
                    .build()
                    .setVariable("a", arg.a)
                    .setVariable("b", arg.b);
    //            .setVariable("n", n);
            double result = e.evaluate();
            return result;
        } catch (Exception e){
            if (e.getMessage().indexOf("Division by zero!")>-1) return null;
            StringBuilder sb = new StringBuilder();

            sb.append("error:");
            sb.append(formula.getFormula());
            sb.append("\t");
            sb.append(arg.toKey(true)).append("-").append(arg.n);
            throw new Exception(sb.toString());
        }
    }

    public Double calculatePoint(ArgVector arg) throws Exception {
//        if (arg.n == 0D) return 0D;
        checkAll();
        try {
            String formula = this.getFormula().getFormula();
            Expression e = new ExpressionBuilder(formula)
//          .variables("a", "b" , "n")
                    .variables("a", "b")
                    .build()
                    .setVariable("a", arg.a)
                    .setVariable("b", arg.b);
//          .setVariable("n", n);
            double result = e.evaluate();
            return result * arg.n;
        } catch (Exception e){
            if (e.getMessage().indexOf("Division by zero!")>-1) return null;
            StringBuilder sb = new StringBuilder();

            sb.append("error:");
            sb.append(this.getFormula().getFormula());
            sb.append("\t");
            sb.append(arg.toKey(true)).append("-").append(arg.n);
            throw new Exception(sb.toString());
        }
    }

    public Formula getFormula() {
        return formula;
    }

    public void setFormula(Formula formula) {
        this.formula = formula;
    }

    public List<ArgVector> getInputData() {
        return inputData;
    }

    public void setInputData(List<ArgVector> inputData) {
        this.inputData = inputData;
    }
}
