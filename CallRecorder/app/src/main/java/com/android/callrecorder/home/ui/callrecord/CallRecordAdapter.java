package com.android.callrecorder.home.ui.callrecord;

import android.app.Activity;
import android.text.TextUtils;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.callrecorder.R;
import com.android.callrecorder.bean.CallItem;
import com.android.callrecorder.bean.CallRecordInfo;
import com.android.callrecorder.manager.RecordPlayerManager;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class CallRecordAdapter extends RecyclerView.Adapter<CallRecordViewHolder> {
    private Activity context;
    private List<CallItem> items = new ArrayList<>();
    private CallItem item;

    public CallRecordAdapter(Activity ctx) {
        context = ctx;
    }

    @NonNull
    @Override
    public CallRecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CallRecordViewHolder(context, parent);
    }

    @Override
    public void onBindViewHolder(@NonNull CallRecordViewHolder holder, int position) {
        item = items.get(position);
        holder.tvCallTime.setText(item.duringStr + " " + item.time);
        holder.tvPhoneNum.setText(TextUtils.isEmpty(item.name) ? item.phone : item.name);
        if (item.callType == CallItem.CALLTYPE_OUT) {
            holder.ivCallType.setImageResource(R.drawable.ic_outgoing);
        } else if (item.callType == CallItem.CALLTYPE_IN) {
            holder.ivCallType.setImageResource(R.drawable.ic_incoming);
        } else if (item.callType == CallItem.CALLTYPE_NO) {
            holder.ivCallType.setImageResource(R.drawable.ic_unkowntype);
        }
        CallRecordInfo callRecordInfo = new CallRecordInfo();
        callRecordInfo.setCallrecod("");
        int during = RecordPlayerManager.getInstance().getDuration(callRecordInfo);
        RecordPlayerManager.getInstance().setSeekBar(holder, during);

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
