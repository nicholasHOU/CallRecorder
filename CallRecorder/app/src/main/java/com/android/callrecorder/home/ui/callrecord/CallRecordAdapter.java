package com.android.callrecorder.home.ui.callrecord;

import android.app.Activity;
import android.text.TextUtils;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.callrecorder.R;
import com.android.callrecorder.home.bean.CallItem;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class CallLogAdapter extends RecyclerView.Adapter<CallLogViewHolder> {
    private Activity context;
    private List<CallItem> items = new ArrayList<>();
    private CallItem item;

    public CallLogAdapter(Activity ctx) {
        context = ctx;
    }

    @NonNull
    @Override
    public CallLogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CallLogViewHolder(context, parent);
    }

    @Override
    public void onBindViewHolder(@NonNull CallLogViewHolder holder, int position) {
        item = items.get(position);
        holder.tvCallTime.setText(item.during + " " + item.time);
        holder.tvPhoneNum.setText(TextUtils.isEmpty(item.name) ? item.phonenum : item.name);
        if (item.callType == CallItem.CALLTYPE_OUT) {
            holder.ivCallType.setImageResource(R.drawable.ic_outgoing);
        } else if (item.callType == CallItem.CALLTYPE_IN) {
            holder.ivCallType.setImageResource(R.drawable.ic_incoming);
        } else if (item.callType == CallItem.CALLTYPE_NO) {
            holder.ivCallType.setImageResource(R.drawable.ic_unkowntype);
        }

    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    public void refreshData(List<CallItem> callItems) {
        if (callItems != null && callItems.size() > 0) {
            this.items.clear();
            this.items.addAll(callItems);
            notifyDataSetChanged();
        }
    }
}
