package com.example;

public class Main {
    public static void main(String[] args) {
        DBTransactionManager db = new DBTransactionManager();
        db.getInstructions("commands.txt");
    }
}