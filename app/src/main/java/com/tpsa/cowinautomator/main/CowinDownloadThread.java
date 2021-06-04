package com.tpsa.cowinautomator.main;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.tpsa.cowinautomator.main.data.CowinJson;
import com.tpsa.cowinautomator.main.data.CowinJsonReader;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class CowinDownloadThread extends Thread {

    private static final long tenMinInMillis = 600000;
    private static final long oneDayInMillis = 86400000;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void run() {
        super.run();

        URL url;

        try {
            while(! Thread.interrupted()) {
                String pattern = "dd-MM-yyyy";
                long currentTimeMillis = System.currentTimeMillis();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                List<CowinJson> cowinJsonList = new ArrayList<>();
                for (int i = 0; i <= 5; i++) {
                    currentTimeMillis += oneDayInMillis;
                    String dateInString = simpleDateFormat.format(new Date(currentTimeMillis));
                    url = new URL("https://cdn-api.co-vin.in/api/v2/appointment/sessions/public/calendarByDistrict?district_id=188&date=" + dateInString);
                    Log.d("CowinAutomator", url.toString());
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    /*String text = new BufferedReader(
                            new InputStreamReader(in, StandardCharsets.UTF_8)).lines()
                            .collect(Collectors.joining("\n"));
                    Log.i("CowinAutomator", text);*/
                    cowinJsonList.add(readStream(in));
                    urlConnection.disconnect();
                }
                CowinMatchThread cowinMatchThread = new CowinMatchThread(cowinJsonList);
                cowinMatchThread.start();
                sleep(tenMinInMillis);
            }
        } catch (Exception e) {
            Log.e("CowinAutomator", e.getMessage(), e);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private CowinJson readStream(InputStream in) throws UnsupportedEncodingException {
        CowinJsonReader cowinJsonReader = new CowinJsonReader(in);
        return cowinJsonReader.readData();
    }
}
