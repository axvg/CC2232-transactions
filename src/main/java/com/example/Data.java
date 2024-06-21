package com.example;

public class Data {
    private String dataName;
    private String lockedBy;

    public Data(String name) {
        this.dataName = name;
        this.lockedBy = "";
    }

    public String lockTransaction() {
        return lockedBy;
    }

    public boolean isLocked() {
        return !lockedBy.isEmpty();
    }

    public void lockData(String transaction) {
        this.lockedBy = transaction;
    }

    public void unlockData() {
        lockedBy = "";
    }

    public String getName() {
        return dataName;
    }
}