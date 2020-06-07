package com.example.kantorapp;

import java.util.ArrayList;
import java.util.List;

public class UserTransaction {

    List<String> transactions = new ArrayList<>();

    public List<String> getMovies() {
        return transactions;
    }

    public void setMovies(List<String> transactions) {
        this.transactions = transactions;
    }
}
