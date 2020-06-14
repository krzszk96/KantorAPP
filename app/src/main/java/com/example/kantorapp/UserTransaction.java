package com.example.kantorapp;

import java.util.ArrayList;
import java.util.List;

public class UserTransaction {

    List<String> transactions = new ArrayList<>();

    public List<String> getTr() {
        return transactions;
    }

    public void setTr(List<String> transactions) {
        this.transactions = transactions;
    }
}
