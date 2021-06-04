package com.tpsa.cowinautomator.main.data;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.telephony.SmsManager;
import android.util.JsonReader;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class CowinJsonReader {
    private JsonReader reader;
    public CowinJsonReader(InputStream in) throws UnsupportedEncodingException {
        reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public CowinJson readData() {
        CowinJson cowinJson = null;
        try {
            reader.beginObject();
            String n = reader.nextName();
            Log.d("CowinAutomator", "First name "+ n);
            cowinJson = readCenters();
            reader.endObject();
            return cowinJson;
        } catch (IOException e){
            Log.e("CowinAutomator", "Failure reading cowin json", e);
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage("9871899779", null, "failed to read json", null, null);
            throw new RuntimeException("Failure reading cowin json", e);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private CowinJson readCenters() throws IOException {
        CowinJson.CowinJsonBuilder cowinJsonBuilder = new CowinJson.CowinJsonBuilder();
        reader.beginArray();
        while (reader.hasNext()) {
            reader.beginObject();
            String centerName = null;
            while (reader.hasNext()) {
                String tag = reader.nextName();
                Log.d("CowinAutomator", "TAG " + tag);
                if ("name".equals(tag)) {
                    centerName = reader.nextString();
                    Log.d("CowinAutomator", "Center name " + centerName);
                    cowinJsonBuilder.addCenter(centerName);
                } else if ("sessions".equals(tag)) {
                    cowinJsonBuilder.addSessions(readSessions(), centerName);
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
        }
        reader.endArray();
        return cowinJsonBuilder.build();
    }

    private List<CowinJson.Session> readSessions() throws IOException {
        List<CowinJson.Session> sessions = new ArrayList<>();
        reader.beginArray();
        while (reader.hasNext()){
            CowinJson.CowinSessionBuilder sessionBuilder = new CowinJson.CowinSessionBuilder();
            reader.beginObject();
            while (reader.hasNext()){
                String tag = reader.nextName();
                Log.d("CowinAutomator", "TAG " + tag);
                if ("date".equals(tag)){
                    String date = reader.nextString();
                    sessionBuilder.setDate(date);
                } else if ("min_age_limit".equals(tag)){
                    int min_age = reader.nextInt();
                    sessionBuilder.setMin_age(min_age);
                } else if ("vaccine".equals(tag)){
                    String vaccine = reader.nextString();
                    sessionBuilder.setVaccine(vaccine);
                } else if ("dose1".equals(tag)){
                    int dose1 = reader.nextInt();
                    sessionBuilder.setDose1(dose1);
                } else if ("dose2".equals(tag)){
                    int dose2 = reader.nextInt();
                    sessionBuilder.setDose2(dose2);
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
            sessions.add(sessionBuilder.build());
        }
        reader.endArray();
        return sessions;
    }
}
