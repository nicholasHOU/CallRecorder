package com.android.callrecorder.home.ui.home;

import android.app.Activity;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.android.callrecorder.home.bean.CallItem;

import java.util.List;

/**
 *
 */
public class CallLogAdapter extends RecyclerView.Adapter<CallViewHolder> {
    private Activity context;
    private List<CallItem> items;
    private CallItem item;

    public CallLogAdapter(Activity ctx) {
        context = ctx;
    }

    @NonNull
    @Override
    public CallViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CallViewHolder(context, parent);
    }

    @Override
    public void onBindViewHolder(@NonNull CallViewHolder holder, int position) {
        item = items.get(position);
        holder.tvCallTime.setText(item.time);
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }
}
