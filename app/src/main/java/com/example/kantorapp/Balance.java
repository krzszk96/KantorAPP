package com.example.kantorapp;

public class Balance {

    private double pln;
    private double eur;
    private double usd;
    private double gbp;

    public Balance(){

    }
    public Balance(double pln, double eur, double usd, double gbp) {
        this.pln = pln;
        this.eur = eur;
        this.usd = usd;
        this.gbp = gbp;
    }

    public double getPln() {
        return pln;
    }
    public double getEur() {
        return eur;
    }
    public double getUsd() {
        return usd;
    }
    public double getGbp() {
        return gbp;
    }

    public void setPln(double pln) {
        this.pln = pln;
    }
    public void setEur(double eur) {
        this.eur = eur;
    }
    public void setUsd(double usd) {
        this.usd = usd;
    }
    public void setGbp(double gbp) {
        this.gbp = gbp;
    }
}
