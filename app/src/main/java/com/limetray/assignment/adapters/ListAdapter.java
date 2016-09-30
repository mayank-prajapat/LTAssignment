package com.limetray.assignment.adapters;

import static com.limetray.assignment.models.Transaction.STATE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.limetray.assignment.MainActivity;
import com.limetray.assignment.R;
import com.limetray.assignment.Util;
import com.limetray.assignment.models.Transaction;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    Context mContext;
    OnItemClickListener mItemClickListener;
    private ArrayList<Transaction> transactionList;
    private RequestQueue queue;

    public ListAdapter(Context context, RequestQueue queue,
            ArrayList<Transaction> transactions) {
        this.mContext = context;
        this.transactionList = transactions;
        this.queue = queue;
    }

    public void setOnItemClickListener(
            final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.row_transactions, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Transaction transaction =
                (Transaction) transactionList.get(position);

        // Update row view's textviews to display transaction information
        holder.stateTextView.setText(transaction.state.toString());
        if (transaction.state == STATE.VERIFIED) {
            holder.stateTextView.setTextColor(mContext.getResources().getColor(
                    R.color.dark_green));
        } else if (transaction.state == STATE.UNVERIFIED) {
            holder.stateTextView.setTextColor(mContext.getResources().getColor(
                    R.color.orange));
        } else if (transaction.state == STATE.FRAUD) {
            holder.stateTextView.setTextColor(mContext.getResources().getColor(
                    R.color.red));
        }

        holder.idTextView.setText(transaction.id);
        holder.descriptionTextView.setText(transaction.description);
        holder.amountTextView.setText(transaction.amount);
        holder.categoryImageView
                .setImageDrawable(mContext
                        .getResources()
                        .getDrawable(
                                (transaction.category.equalsIgnoreCase("Taxi") ? R.drawable.taxi_icon
                                                                              : R.drawable.recharge_icon)));
        holder.timestampTextView.setText(Util
                .getHumanReadableDate(transaction.timestamp));
    }

    public void onTransactionListUpdate(ArrayList<Transaction> transactionList) {
        this.transactionList = transactionList;
        notifyDataSetChanged();
    }

    public void onTransactionUpdate(int position, STATE newState) {
        transactionList.get(position).state = newState;
        notifyItemChanged(position);
        try {
            Util.getTransactionsJson().getJSONArray("expenses")
                    .getJSONObject(position).put("state", newState);
            JsonObjectRequest putRequest =
                    new JsonObjectRequest(Request.Method.PUT,
                                          MainActivity.DATA_FETCH_URL,
                                          Util.getTransactionsJson(),
                                          new Response.Listener<JSONObject>() {
                                              @Override
                                              public void onResponse(
                                                      JSONObject response) {
                                                  // TODO
                                              }
                                          }, new Response.ErrorListener() {
                                              @Override
                                              public void onErrorResponse(
                                                      VolleyError error) {
                                                  // error
                                              }
                                          }) {

                        @Override
                        public Map<String, String> getHeaders()
                                throws AuthFailureError {
                            Map<String, String> params =
                                    new HashMap<String, String>();
                            params.put("Content-Type",
                                    "application/json; charset=utf-8");
                            params.put("Accept",
                                    "application/json; charset=utf-8");
                            return params;
                        }
                    };
            queue.add(putRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {
        public TextView idTextView;
        public TextView descriptionTextView;
        public TextView amountTextView;
        public ImageView categoryImageView;
        public TextView timestampTextView;
        public TextView stateTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            idTextView = (TextView) itemView.findViewById(R.id.transaction_id);
            descriptionTextView =
                    (TextView) itemView
                            .findViewById(R.id.transaction_description);
            amountTextView =
                    (TextView) itemView.findViewById(R.id.transaction_amount);
            categoryImageView =
                    (ImageView) itemView
                            .findViewById(R.id.transaction_category);
            timestampTextView =
                    (TextView) itemView
                            .findViewById(R.id.transaction_timestamp);
            stateTextView =
                    (TextView) itemView.findViewById(R.id.transaction_state);
            stateTextView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(itemView, getAdapterPosition());
            }
        }
    }
}