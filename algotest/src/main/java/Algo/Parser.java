package Algo;




import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by WSY on 13/7/16.
 */
public class Parser {


    public static final String directory = "/Users/WangSiyuan/Desktop/tripqData/";
    public static final String file = "bc24c87e6124_20160712_213045.json";

    public static Map<Long, Integer> timeToIndex;

    public static void main(String[] args) throws IOException, JSONException {
        plotIndex(getAccel(directory + file));
    }

    public static void plotIndex(Double[] array){
        int total = array.length;
        int peakL = array[total-1].intValue();
        int l = total - peakL;


    }

    public static Double[] getBumpIndex(String file) throws IOException, JSONException {
        String fileName = file;
        //System.out.println(fileName);
        File f1 = new File(fileName);
        //File f1 = new File("D:/upload/"+fileName);
        String tempFileString = FileUtils.readFileToString(f1);
        JSONObject jsonObject;
        jsonObject = new JSONObject(tempFileString);
        JSONArray bumpArray = jsonObject.getJSONArray("BUMP");
        ArrayList<Double> bumpIndexList = new ArrayList<>();


        for(int i=0; i<bumpArray.length(); i++){

            long timeStamp = bumpArray.getJSONObject(i).getLong("TIME") / 1000;
            while(!timeToIndex.keySet().contains(timeStamp)){
                timeStamp += 1;
            }
            bumpIndexList.add(timeToIndex.get(timeStamp).doubleValue());


        }

        Double[] rstArray = bumpIndexList.toArray(new Double[bumpArray.length()]);

        return rstArray;



    }

    public static Double[] getJamIndex(String file) throws IOException, JSONException {
        String fileName = file;
        //System.out.println(fileName);
        File f1 = new File(fileName);
        //File f1 = new File("D:/upload/"+fileName);
        String tempFileString = FileUtils.readFileToString(f1);
        JSONObject jsonObject;
        jsonObject = new JSONObject(tempFileString);
        JSONArray bumpArray = jsonObject.getJSONArray("JAM");
        ArrayList<Double> bumpIndexList = new ArrayList<>();


        System.out.println("GET Jam Index:");
        for(int i=0; i<bumpArray.length(); i++){
            long timeStamp = bumpArray.getJSONObject(i).getLong("TIME") / 1000;
            //System.out.println(timeStamp);
            while(!timeToIndex.keySet().contains(timeStamp)){
                timeStamp += 1;
//                System.out.println("No key. Time stamp incremented to "+timeStamp);
            }
            bumpIndexList.add(timeToIndex.get(timeStamp).doubleValue());
        }

        Double[] rstArray = bumpIndexList.toArray(new Double[bumpArray.length()]);

        return rstArray;



    }


    public static Double[] getAccel(String file) throws IOException, JSONException {

        timeToIndex = new HashMap<>();

        //System.out.println("/getaccel");
        String fileName = file;
        //System.out.println(fileName);
        File f1 = new File(fileName);
        //File f1 = new File("D:/upload/"+fileName);
        String tempFileString = FileUtils.readFileToString(f1);
        JSONObject jsonObject;
        JSONArray accelArray = null;
        jsonObject = new JSONObject(tempFileString);
        //here changes//accelArray = jsonObject.getJSONArray("ACCE_DATA");
        accelArray = jsonObject.getJSONArray("ACCE_ABSOLUTE");

        ArrayList<Double> accelAll;
        ArrayList<Integer> peakIndexArr = new ArrayList<Integer>();


        if(accelArray!=null){
            int l = accelArray.length();
            //System.out.println(l);
            Double accelIndexArray[] = new Double[l];
            Double accelRawArray[] = getAccelRaw(file);



            for(int i=0;i<l;i++){
                //accelIndexArray[i] = accelArray.getJSONObject(i).getDouble("INDEX");

                accelIndexArray[i] =  (double) calculateIndex(accelRawArray[i], accelRawArray[i+l], accelRawArray[i+2*l]);



                timeToIndex.put(accelArray.getJSONObject(i).getLong("TIME")/1000, i);



            }



            peakIndexArr = AlgoUtil.peakSearch(accelIndexArray);
            int peakArrSize = peakIndexArr.size();
            accelAll = new ArrayList<Double>();
            int totalSize = l+peakArrSize+1;
            for(int i=0;i<totalSize;i++){
                //TQI index data points
                if(i<l){
                    accelAll.add(accelIndexArray[i]);
                    continue;
                }
                //peak indices
                else if(i>=l&&i!=(totalSize-1)){
                    accelAll.add(Double.valueOf((double)peakIndexArr.get(i-l)));
                    continue;
                }
                //number of peaks
                else{
                    accelAll.add(Double.valueOf((double)peakArrSize));
                }
            }

            Double rstArr[] = new Double[totalSize];
            return accelAll.toArray(rstArr);



        }else{
            return null;
        }

    }

    public static int calculateIndex(double x, double y, double z){


        //////////////////////////////
        //
        //
        //////////////////////////////
        // previous one 1.4 only
//        double sum = 1.4 * x * x + 1.4 * y * y + z * z;
        double sum = z * z;
        double level = StrictMath.log10(sum) * 10 + 100;



        int min = 100;
        int max = 120;
        int grad = 5;

        if (sum != 0) {
            if(level<=max){
                return (level < min ? 0 : (int) ((level - min)*grad));
            }else{
                return 100;
            }
        }else{
            return 0;
        }
    }

    public static Double[] getAccelRaw(String file) throws IOException {

        System.out.println("/getaccelraw");
        String fileName = file;
        System.out.println(fileName);
        File f1 = new File(fileName);
        //File f1 = new File("D:/upload/"+fileName);
        String tempFileString = FileUtils.readFileToString(f1);
        JSONObject jsonObject;
        JSONArray accelArray = null;

        try {
            jsonObject = new JSONObject(tempFileString);
            //change here// accelArray = jsonObject.getJSONArray("ACCE_DATA");
            accelArray = jsonObject.getJSONArray("ACCE_ABSOLUTE");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        ArrayList<Double> accelAll;


        if(accelArray!=null){

            //####################################
            //This is to obtain the accel raw data//
            //####################################


            int l = accelArray.length();
            //System.out.println(l);
            Double accelAllArray[] = new Double[3*l];
            accelAll = new ArrayList<Double>();
            for(int i=0;i<3*l;i++){
                accelAll.add(0.0);
            }

            for(int i=0;i<l;i++){
                try {
                    accelAll.set(i, accelArray.getJSONObject(i).getDouble("X"));
                    accelAll.set(l+i, accelArray.getJSONObject(i).getDouble("Y"));
                    accelAll.set(2*l+i, accelArray.getJSONObject(i).getDouble("Z"));
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            return accelAll.toArray(accelAllArray);


        }else{
            return null;
        }

    }



}
