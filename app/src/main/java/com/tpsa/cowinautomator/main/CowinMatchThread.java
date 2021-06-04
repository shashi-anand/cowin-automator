package com.tpsa.cowinautomator.main;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.telephony.SmsManager;
import android.util.Log;

import com.tpsa.cowinautomator.main.data.CowinJson;

import java.util.List;
import java.util.stream.Collectors;

public class CowinMatchThread extends Thread {
    private final List<CowinJson> cowinJsonList;


    public CowinMatchThread(List<CowinJson> cowinJsonList){
        this.cowinJsonList = cowinJsonList;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void run(){
        StringBuilder stringBuilder = new StringBuilder();
        cowinJsonList.stream().forEach(cowinJson ->
                cowinJson.getCowinCenters().stream().forEach(center -> {
                    List<CowinJson.Session> matchedSession = match(center.getSessions());
                    if (! matchedSession.isEmpty()){
                        stringBuilder.append("Center: ").append(center.getName()).append("\n");
                        matchedSession.stream().forEach(session -> stringBuilder.
                                append("Date: ").append(session.getDate()).
                                append(" Vaccine: ").append(session.getVaccine()).
                                append(" Dose1: ").append(session.getDose1()).
                                append(" Dose2: ").append(session.getDose2()).
                                append("\n"));
                    }
                })
        );
        if (stringBuilder.length() != 0){
            Log.i("CowinAutomator", "MATCH MATCH MATCH" + stringBuilder.toString());
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage("9871899779", null, stringBuilder.toString(), null, null);
            smsManager.sendTextMessage("9717594590", null, stringBuilder.toString(), null, null);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    List<CowinJson.Session> match( List<CowinJson.Session> sessionList){
        return sessionList.stream().filter(session ->
                session.getVaccine().equals("COVAXIN") && (session.getDose1() > 0 || session.getDose2() > 0)).collect(Collectors.toList());
    }

}
