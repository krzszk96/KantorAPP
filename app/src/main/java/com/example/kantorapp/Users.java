package com.example.kantorapp;

public class Users {

    private String id;
    private String email, passwd;
    private double accbalance;
    //private String username;

    public Users(){

    }

    public Users(String id, String email, String passwd, double accbalance) {
        this.id = id;
        //this.username = username;
        this.email = email;
        this.passwd = passwd;
        this.accbalance = accbalance;
    }

    public String getId() {
        return id;
    }
    /*public String getUsername() {
        return username;
    }*/
    public String getEmail() {
        return email;
    }
    public String getPasswd() {
        return passwd;
    }
    public double getAccbalance() {
        return accbalance;
    }

    public void setId(String id) {
        this.id = id;
    }
    /*public void setUsername(String username) {
        this.username = username;
    }*/
    public void setEmail(String email) {
        this.email = email;
    }
    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }
    public void setAccbalance(double accbalance) {
        this.accbalance = accbalance;
    }
}
