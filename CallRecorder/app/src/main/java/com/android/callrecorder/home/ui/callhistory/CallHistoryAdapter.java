package com.android.callrecorder.home.ui.callhistory;

import android.app.Activity;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.callrecorder.bean.response.CallHistoryResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * 通话历史统计
 */
public class CallHistoryAdapter extends RecyclerView.Adapter<CallHistoryViewHolder> {
    private Activity context;
    private List<CallHistoryResponse.CallLog> items = new ArrayList<>();
    private CallHistoryResponse.CallLog item;

    public CallHistoryAdapter(Activity ctx) {
        context = ctx;
    }

    @NonNull
    @Override
    public CallHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CallHistoryViewHolder(context, parent);
    }

    @Override
    public void onBindViewHolder(@NonNull CallHistoryViewHolder holder, int position) {
        item = items.get(position);
        holder.tvCallDay.setText(item.day);
        holder.tvCallTimes.setText(item.total_number);
        int minutes = (item.total_time / 60);
        int seconds = (item.total_time % 60);
        String minute = minutes == 0 ? "" : minutes + "分";
        String second = seconds + "秒";
        holder.tvCallTimeDuring.setText(minute + second);
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    public void refreshData(List<CallHistoryResponse.CallLog> callItems) {
        if (callItems != null && callItems.size() > 0) {
            this.items.addAll(callItems);
            notifyDataSetChanged();
        }
    }
}
