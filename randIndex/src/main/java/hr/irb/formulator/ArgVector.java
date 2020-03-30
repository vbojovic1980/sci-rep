package hr.irb.formulator;

import com.sun.org.apache.xpath.internal.Arg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ArgVector {
    public Double a;
    public Double b;
    public Double n;

    public ArgVector(Double a, Double b , Double n, boolean order){
        setAll(a,b,n,order);
    }

    public static String toKey(Double a, Double b, boolean useBrackets){
        String key = "";
        if (useBrackets) key=key+"[";
        String intA = Integer.toString(a.intValue());
        String intB = Integer.toString(b.intValue());
        key=key+ (
            (a<=b)
            ? intA+","+intB
            : intB+","+intA
        );
        if (useBrackets) key=key+"]";
        return key;
    }

    public String toKey(boolean useBrackets){
        return toKey(this.a, this.b,useBrackets);
    }

    private void setAll(Double a, Double b , Double n, boolean order){
        if (order && a>b){
            this.b=a;
            this.a=b;
        } else{
            this.a=a;
            this.b=b;
        }
        this.n=n;
    }

    public ArgVector(){

    }

    public ArgVector(String pairString, Double n, boolean order){
        String tmp = pairString
            .replace("[","")
            .replace("]","")
            .replace(" ","");

        Double a = Double.parseDouble(tmp.split(",")[0]);
        Double b = Double.parseDouble(tmp.split(",")[1]);
        setAll(a,b,n, order);
    }
    public static List<ArgVector> sumarizeVector(List<ArgVector> vectorList){
        HashMap<String,Double> map = new HashMap<>();
        List<ArgVector> uniqList = new ArrayList<>();

        for(ArgVector arg : vectorList){

            String key = arg.toKey(false);
            if (map.containsKey(key)){
                Double val = map.get(key)+arg.n;
                map.replace(key,val);
            }else {
                map.put(key,arg.n);
            }
        }

        for(String key : map.keySet()){
            ArgVector vec = new ArgVector(key,map.get(key),true);
            uniqList.add(vec);
        }

        return uniqList;
    }

}
