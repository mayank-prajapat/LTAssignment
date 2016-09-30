package com.limetray.assignment;

import static com.limetray.assignment.models.Transaction.STATE;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.limetray.assignment.models.Transaction;

public class Util {
    private static JSONObject transactionsJson;

    public static ArrayList<Transaction> retrieveTransactionsFromResponse(
            String response) throws JSONException {
        ArrayList<Transaction> transactionList = new ArrayList<>();
        if (response == null) {
            return transactionList;
        }

        JSONArray jsonArray;
        setTransactionsJson(new JSONObject(response));
        if (getTransactionsJson().has("expenses")) {
            jsonArray = getTransactionsJson().getJSONArray("expenses");
        } else {
            return transactionList;
        }

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            if (jsonObject != null) {
                Transaction transaction = new Transaction();
                if (jsonObject.has("id")) {
                    transaction.id = jsonObject.getString("id");
                }
                if (jsonObject.has("description")) {
                    transaction.description =
                            jsonObject.getString("description");
                }
                if (jsonObject.has("amount")) {
                    transaction.amount = jsonObject.getString("amount");
                }
                if (jsonObject.has("category")) {
                    transaction.category = jsonObject.getString("category");
                }
                if (jsonObject.has("timestamp")) {
                    transaction.timestamp = jsonObject.getInt("timestamp");
                }
                if (jsonObject.has("state")) {
                    try {
                        transaction.state =
                                STATE.valueOf(jsonObject.getString("state"));
                    } catch (IllegalArgumentException ex) {
                        transaction.state = STATE.UNVERIFIED;
                    }

                }

                transactionList.add(transaction);
            }
        }
        return transactionList;
    }

    public static JSONObject getTransactionsJson() {
        return transactionsJson;
    }

    public static void setTransactionsJson(JSONObject transactionsJson) {
        Util.transactionsJson = transactionsJson;
    }

    public static String getHumanReadableDate(long epochTime) {
        String pattern = "E, MMM dd, hh:mm a";
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        Date date = new Date(epochTime * 1000);
        return format.format(date);
    }
}