package com.limetray.assignment;

import static com.limetray.assignment.models.Transaction.STATE;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.ArrayAdapter;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.limetray.assignment.adapters.ListAdapter;
import com.limetray.assignment.models.Transaction;

public class MainActivity extends FragmentActivity implements
        ListAdapter.OnItemClickListener, DownloadCompleteListener {
    public static final String DATA_FETCH_URL =
            "https://jsonblob.com/api/jsonBlob/57bf2a88e4b0dc55a4f0a7b6";
    ProgressDialog mProgressDialog;
    private ListAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager mStaggeredLayoutManager;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mStaggeredLayoutManager =
                new StaggeredGridLayoutManager(
                                               1,
                                               StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mStaggeredLayoutManager);

        queue = Volley.newRequestQueue(this);

        if (isNetworkConnected()) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Please wait...");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
            startDownload(true);
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("No Internet Connection")
                    .setMessage(
                            "It looks like your internet connection is off. Please turn it "
                                    + "on and try again")
                    .setPositiveButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                        int which) {}
                            }).setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        scheduleDataSync();
    }

    @Override
    public void onItemClick(View view, final int position) {
        AlertDialog.Builder stateSelector = new AlertDialog.Builder(this);
        stateSelector.setIcon(R.mipmap.ic_launcher);
        stateSelector.setTitle("Select new state for this transaction:");

        final ArrayAdapter<STATE> arrayAdapter =
                new ArrayAdapter<STATE>(
                                        this,
                                        android.R.layout.select_dialog_singlechoice,
                                        STATE.values());

        stateSelector.setNegativeButton("cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        stateSelector.setAdapter(arrayAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAdapter.onTransactionUpdate(position,
                                arrayAdapter.getItem(which));
                    }
                });
        stateSelector.show();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private void startDownload(final boolean onStartup) {

        StringRequest stringRequest =
                new StringRequest(
                                  Request.Method.GET,
                                  DATA_FETCH_URL,
                                  new com.android.volley.Response.Listener<String>() { // 2
                                      @Override
                                      public void onResponse(String response) {
                                          try {
                                              downloadComplete(
                                                      onStartup,
                                                      Util.retrieveTransactionsFromResponse(response));
                                          } catch (JSONException e) {
                                              e.printStackTrace();
                                          }
                                      }
                                  },
                                  new com.android.volley.Response.ErrorListener() {
                                      @Override
                                      public void onErrorResponse(
                                              VolleyError error) {}
                                  });
        queue.add(stringRequest);
    }

    @Override
    public void downloadComplete(boolean onStartup,
            ArrayList<Transaction> transactionList) {
        if (onStartup) {
            mAdapter = new ListAdapter(this, queue, transactionList);
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.setOnItemClickListener(this);
            if (mProgressDialog != null) {
                mProgressDialog.hide();
            }
        } else {
            mAdapter.onTransactionListUpdate(transactionList);
        }
    }

    public void scheduleDataSync() {
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask scheduleDataSyncTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            if (isNetworkConnected()) {
                                startDownload(false);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };
        timer.schedule(scheduleDataSyncTask, 10000, 10000);
    }
}