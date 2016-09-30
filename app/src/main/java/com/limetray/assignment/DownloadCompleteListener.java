package com.limetray.assignment;

import com.limetray.assignment.models.Transaction;

import java.util.ArrayList;

public interface DownloadCompleteListener {
    void downloadComplete(boolean onStartup, ArrayList<Transaction> transactions);
}