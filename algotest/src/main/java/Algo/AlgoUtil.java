package Algo;

import java.util.ArrayList;

/**
 * Created by WSY on 13/7/16.
 */
public class AlgoUtil {

    public static ArrayList<Integer> peakSearch(Double [] signalArr){

        int l = signalArr.length;
        ArrayList<Integer> peakIndexArr = new ArrayList<Integer>();
        int k = 15;//windows size for each side
        double h = 2;//constant for the threshold

        Double[] peakFunc1Values = peakFunc1(signalArr, k);
        double standardDeviation = standardDeviationCalc(signalArr);
        double expectedValue = expectedValueCalc(signalArr);

        for(int i = 0; i<l; i++){
            if(signalArr[i]>30 && peakFunc1Values[i]>0 && (peakFunc1Values[i] - expectedValue)>h*standardDeviation){
                peakIndexArr.add(i);
            }
        }

        // retain only one peak out of any set of peaks within distance k of each other
        //update: change to k/2 window size

        for(int i=0; i<peakIndexArr.size()-1; i++){
            if(peakIndexArr.get(i+1)-peakIndexArr.get(i)<= k / 1.5){
                if(signalArr[peakIndexArr.get(i+1)]>signalArr[peakIndexArr.get(i)]){
                    peakIndexArr.remove(i);
                }else{
                    peakIndexArr.remove(i+1);
                }
                i--;
            }
        }

        return peakIndexArr;
    }


    public static double standardDeviationCalc(Double [] signalArr){

        double mean = expectedValueCalc(signalArr);
        int l = signalArr.length;
        if(l==0){
            return -1;
        }
        int i = 0;
        double sd = 0;
        while(i<l){
            sd+=Math.pow(signalArr[i]- mean, 2);
            i++;
        }
        double rst = Math.sqrt(sd/(double)l);
        return rst==0.0 ? 0.000000001 : rst;

    }

    public static double expectedValueCalc(Double [] signalArr){

        double sum = 0;
        int l = signalArr.length;
        if(0==l){
            return -1;
        }
        int i=0;
        while(i<l){
            sum+=signalArr[i];
            i++;
        }

        return sum/l;
    }

    //S1(k,i,xi,T) = (lmax + rmax) / 2
    public static Double[] peakFunc1(Double[] signalArr, int k){

        int l = signalArr.length;
        Double[] peakFunc1Values = new Double[l];

        for(int i=0; i<l; i++){
            double lmax = Double.MIN_VALUE;
            double rmax = Double.MIN_VALUE;

            for(int j=i-k; j<i; j++){
                if(j>=0){
                    if(signalArr[i]-signalArr[j]>lmax){
                        lmax = signalArr[i]-signalArr[j];
                    }
                }
            }

            for(int j=i+1;j<=i+k;j++){
                if(j<=l-1){
                    if(signalArr[i]-signalArr[j]>rmax){
                        rmax = signalArr[i]-signalArr[j];
                    }
                }
            }

            peakFunc1Values[i]= (lmax+rmax)/2;


        }

        return peakFunc1Values;
    }
}
