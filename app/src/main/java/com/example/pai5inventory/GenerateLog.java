package com.example.pai5inventory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

public class GenerateLog {

    public static void main(String[] args) throws IOException {

        System.out.println(getResultOfLastMonth());

    }


    private static String getResultOfLastMonth(){

        Database e = new Database();

        String res = "";
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        Date minusone = cal.getTime();

        Calendar cal2 = Calendar.getInstance();
        cal.add(Calendar.MONTH, -2);
        Date minustwo = cal.getTime();

        List<String> ordersSet = e.getAllOrdersInRange(minusone,new Date(System.currentTimeMillis()-100));

        Double conexionesOk = 0.0;

        for(String i:ordersSet){
            Boolean estado = Boolean.valueOf(i);

            if(estado){
                conexionesOk = conexionesOk +1.0;
            }

        }


        Double comparatorthisMonth = (100*conexionesOk)/ordersSet.size();



        List<String> ordersSet2 = e.getAllOrdersInRange(minustwo,minusone);

        Double conexionesOk2 = 0.0;

        for(String i:ordersSet2){
            Boolean estado = Boolean.valueOf(i);

            if(estado){
                conexionesOk = conexionesOk +1.0;
            }

        }



        Double comparatorlastMonth = (100*conexionesOk2)/ordersSet2.size();

        String tendencia = "";
        if(comparatorthisMonth<comparatorlastMonth){
            tendencia = "NEGATIVA";
        }else if(comparatorthisMonth>comparatorlastMonth){
            tendencia= "POSITIVA";
        }else {
            tendencia = "NULA";
        }

        res = "El porcentaje de este mes es " + comparatorthisMonth + " con una tendencia " + tendencia;

        return res;
    }

}
