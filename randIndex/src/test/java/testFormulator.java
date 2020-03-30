import hr.irb.formulator.ArgVector;
import hr.irb.formulator.Calculator;
import hr.irb.formulator.Formula;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class testFormulator {
    private List<ArgVector> arg1_7 = new ArrayList<ArgVector>();
    private List<ArgVector> argC_6 = new ArrayList<>();
    @Before
    public void setUp(){
        for (int i = 1; i<=7 ; i++){
            for (int j = i; j<=7 ; j++){
                ArgVector v = new ArgVector();
                v.a = new Double(i);
                v.b = new Double(j);
                v.n = 1D;
                this.arg1_7.add(v);
            }
        }
        this.argC_6.add(new ArgVector("1,2", (double) 1,true));
        this.argC_6.add(new ArgVector("1,3", (double) 2,true));
        this.argC_6.add(new ArgVector("2,3", (double) 1,true));
    }
//    @Test
    public  void testE() throws Exception {
        List<Double> results = new ArrayList<Double>();
//        Formula formula = new Formula("exp(a)+exp(b)",null);
        Formula formula = new Formula("exp(10)",null);
        Calculator c = new Calculator(formula,this.arg1_7);
        results = c.getAll();
        Assert.assertTrue(results.size()>0);
        Assert.assertEquals(this.arg1_7.size(), results.size());
        printSourceAndResult(this.arg1_7, results);
    }

//    @Test
    public  void testPower() throws Exception {
        List<Double> results = new ArrayList<Double>();
//        Formula formula = new Formula("exp(a)+exp(b)",null);
        Formula formula = new Formula("(a^4)+(b^2)",null);
        Calculator c = new Calculator(formula,this.arg1_7);
        results = c.getAll();
        Assert.assertTrue(results.size()>0);
        Assert.assertEquals(this.arg1_7.size(), results.size());
        printSourceAndResult(this.arg1_7, results);
    }

//    @Test
    public  void testLogLn() throws Exception {
        List<Double> results = new ArrayList<Double>();
        Formula formula = new Formula("log(a)+ln(b)",null);
        Calculator c = new Calculator(formula,this.arg1_7);
        results = c.getAll();
        Assert.assertTrue(results.size()>0);
        Assert.assertEquals(this.arg1_7.size(), results.size());
        printSourceAndResult(this.arg1_7, results);
    }

//    @Test
    public  void testSinCos() throws Exception {
        List<Double> results = new ArrayList<Double>();
        Formula formula = new Formula("sin(a)+cos(b)",null);
        Calculator c = new Calculator(formula,this.arg1_7);
        results = c.getAll();
        Assert.assertTrue(results.size()>0);
        Assert.assertEquals(this.arg1_7.size(), results.size());
        printSourceAndResult(this.arg1_7, results);
    }

//    @Test
    public  void test2x() throws Exception {
        List<Double> results = new ArrayList<Double>();
        Formula formula = new Formula("2a+3b",null);
        Calculator c = new Calculator(formula,this.arg1_7);
        results = c.getAll();

        Assert.assertTrue(results.size()>0);
        Assert.assertEquals(this.arg1_7.size(), results.size());
        printSourceAndResult(this.arg1_7, results);
    }

//    @Test
    public  void testSqrt() throws Exception {
        List<Double> results = new ArrayList<Double>();
        Formula formula = new Formula("sqrt(2a+3b)+abs(a-b)",null);
        Calculator c = new Calculator(formula,this.arg1_7);
        results = c.getAll();

        Assert.assertTrue(results.size()>0);
        Assert.assertEquals(this.arg1_7.size(), results.size());
        printSourceAndResult(this.arg1_7, results);
    }


    private void printSourceAndResult(List<ArgVector> src , List<Double> res) throws Exception {
        if (res.size() != src.size()  || res.size() == 0 || src.size()==0)
            throw new Exception("No data");
        for (int i =0 ; i< src.size() ; i++){
            String row = String.format("[%s,%s]\t%s"
                ,Double.toString(src.get(i).a)
                ,Double.toString(src.get(i).b)
                ,Double.toString(res.get(i)));

            System.out.print(row+"\n");
        }
    }

    @Test
    public  void testXminus2Y() throws Exception {
        List<Double> results1 = new ArrayList<Double>();
        List<Double> results2 = new ArrayList<Double>();

        Formula formula = new Formula("x-2y","x-2y");
        Calculator c1 = new Calculator(formula,this.arg1_7);
        results1 = c1.getAll();

        printSourceAndResult(this.arg1_7, results1);

        formula = new Formula("y-2x","y-2x");
        Calculator c2 = new Calculator(formula,this.arg1_7);
        results2 = c2.getAll();
        printSourceAndResult(this.arg1_7, results2);
    }
    @Test
    public  void testXplus2Y() throws Exception {
        List<Double> results1 = new ArrayList<Double>();
        List<Double> results2 = new ArrayList<Double>();

        Formula formula = new Formula("x+2y", "x+2y");
        Calculator c1 = new Calculator(formula, this.arg1_7);
        results1 = c1.getAll();

        printSourceAndResult(this.arg1_7, results1);

        formula = new Formula("y+2x", "y+2x");
        Calculator c2 = new Calculator(formula, this.arg1_7);
        results2 = c2.getAll();
        printSourceAndResult(this.arg1_7, results2);
    }


    @Test
    public  void testABS() throws Exception {
        //this test will not work beacuse pwoer function should be written as x^y
        List<Double> results1 = new ArrayList<Double>();
//        Formula formula = new Formula("|power(a,0.25)-power(b,0.25)|", "abs");
//        Formula formula = new Formula("abs(power(a,0.25)-power(b,0.25))", "abs");
//        Formula formula = new Formula("abs(power(a,0.25)-power(b,0.25))", "abs");

//        Formula formula = new Formula("power(a,2)-power(b,2)", "pow");
        Formula formula = new Formula("abs((a^0.25)-(b^0.25))", "pow");
        Calculator c1 = new Calculator(formula, this.arg1_7);
        results1 = c1.getAll();

        printSourceAndResult(this.arg1_7, results1);

    }


    @Test
    public  void testArgC6() throws Exception {
        List<Double> results1 = new ArrayList<Double>();
        List<Double> results2 = new ArrayList<Double>();

        Formula formula = new Formula("x-2y", "x-2y");
        Calculator c1 = new Calculator(formula, this.argC_6);
        results1 = c1.getAll();

        printSourceAndResult(this.argC_6, results1);

        formula = new Formula("y-2x", "y-2x");
        Calculator c2 = new Calculator(formula, this.argC_6);
        results2 = c2.getAll();
        printSourceAndResult(this.argC_6, results2);
    }
}
