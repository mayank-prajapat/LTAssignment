package com.limetray.assignment.models;

public class Transaction {

    public String id;
    public String description;
    public String amount;
    public String category;
    public long timestamp;
    public STATE state;

    public enum STATE {
        VERIFIED, UNVERIFIED, FRAUD
    }
}
