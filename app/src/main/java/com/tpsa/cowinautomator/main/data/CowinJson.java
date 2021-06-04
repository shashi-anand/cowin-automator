package com.tpsa.cowinautomator.main.data;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CowinJson {
    public List<Center> getCowinCenters() {
        return cowinCenters;
    }

    List<Center> cowinCenters;

    public CowinJson(List<Center> cowinCenters){
        this.cowinCenters = cowinCenters;
    }



    public static class Center {
        public Center(String name) {
            this.name = name;
            this.sessions = new ArrayList<>();
        }

        public String getName() {
            return name;
        }

        public List<Session> getSessions() {
            return sessions;
        }

        String name;
        List<Session> sessions;

        public void addSession(Session session){
            this.sessions.add(session);
        }

        public void setSessions(List<Session> sessionList){
            this.sessions = sessionList;
        }
    }

    public static class Session {
        String date;
        String vaccine;
        int min_age;
        int dose1;
        int dose2;

        public String getDate() {
            return date;
        }

        public String getVaccine() {
            return vaccine;
        }

        public int getMin_age() {
            return min_age;
        }

        public int getDose1() {
            return dose1;
        }

        public int getDose2() {
            return dose2;
        }

        public Session(String date, String vaccine, int min_age, int dose1, int dose2) {
            this.date = date;
            this.vaccine = vaccine;
            this.min_age = min_age;
            this.dose1 = dose1;
            this.dose2 = dose2;
        }

    }

    public static class CowinJsonBuilder {
        List<Center> cowinCenters;

        public CowinJsonBuilder(){
            cowinCenters = new ArrayList<>();
        }

        public CowinJsonBuilder addCenter(String name){
            cowinCenters.add(new Center(name));
            return this;
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        public CowinJsonBuilder addSession(Session session, final String centerName){
            Optional<Center> cowinCenter = cowinCenters.stream().filter(center -> center.name.equals(centerName)).findAny();
            cowinCenter.get().addSession(session);
            return this;
        }

        public CowinJson build(){
            return new CowinJson(cowinCenters);
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        public CowinJsonBuilder addSessions(List<Session> sessions, String centerName) {
            Log.d("CowinAutomator", "Centers while adding session " + cowinCenters.stream().map(c  -> c.getName()).collect(Collectors.joining(", ")));
            Log.d("CowinAutomator", "Adding session for center " + centerName);
            Optional<Center> cowinCenter = cowinCenters.stream().filter(center -> center.name.equals(centerName)).findAny();
            cowinCenter.get().setSessions(sessions);
            return this;
        }
    }

    public static class CowinSessionBuilder {
        private String date;
        String vaccine;
        int min_age;
        int dose1;
        int dose2;

        public CowinSessionBuilder setDate(String date) {
            this.date = date;
            return this;
        }

        public CowinSessionBuilder setVaccine(String vaccine) {
            this.vaccine = vaccine;
            return this;
        }

        public CowinSessionBuilder setMin_age(int min_age) {
            this.min_age = min_age;
            return this;
        }

        public CowinSessionBuilder setDose1(int dose1) {
            this.dose1 = dose1;
            return this;
        }

        public CowinSessionBuilder setDose2(int dose2) {
            this.dose2 = dose2;
            return this;
        }

        public Session build(){
            return new Session(date, vaccine, min_age, dose1, dose2);
        }
    }
}
