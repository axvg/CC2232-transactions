package com.example;

//2- suspended
//1 - executed
//0 - intialized

public class Transaction {
    private String transactionName;
    private int status;
    public int order;

    public Transaction(String transactionName) {
        this.transactionName = transactionName;
        this.status = 0;
        this.order = 0;
    }

    public void suspend() {
        this.status = 2;
    }

    public void execute() {
        this.status = 1;
    }

    public boolean isExecuted() {
        return this.status == 1;
    }

    public String getName() {
        return this.transactionName;
    }
}