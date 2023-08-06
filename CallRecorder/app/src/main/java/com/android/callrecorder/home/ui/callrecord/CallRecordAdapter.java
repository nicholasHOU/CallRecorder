package com.android.callrecorder.home.ui.callrecord;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.callrecorder.R;
import com.android.callrecorder.bean.CallItem;
import com.android.callrecorder.bean.CallRecordInfo;
import com.android.callrecorder.config.Constant;
import com.android.callrecorder.manager.RecordPlayerManager;
import com.android.callrecorder.utils.DateUtil;

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
        if (item.callType == CallItem.CALLTYPE_OUT) {
            holder.ivCallType.setImageResource(R.drawable.ic_outgoing);
        } else if (item.callType == CallItem.CALLTYPE_IN) {
            holder.ivCallType.setImageResource(R.drawable.ic_incoming);
        } else if (item.callType == CallItem.CALLTYPE_NO) {
            holder.ivCallType.setImageResource(R.drawable.ic_unkowntype);
        } else if (item.callType == CallItem.CALLTYPE_REJECT) {
            holder.ivCallType.setImageResource(R.drawable.ic_incoming);
        }
        String callRecordPath = item.recordPath;
        int during = RecordPlayerManager.getInstance().getDuration(callRecordPath);
        String totalTime = DateUtil.formatTime(false, during);
        RecordPlayerManager.getInstance().setSeekBar(holder, during);
        holder.tvPlayTotalTime.setText(totalTime);
        holder.tvCallTime.setText(totalTime + " " + item.timeStr);
        holder.tvPhoneNum.setText(TextUtils.isEmpty(item.name) ? item.phone : item.name);
        holder.setTag(item);
        holder.ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.getPlayPosition() != -1) {//判断当前是否在播放，如果有播放的,先暂停播放中的task

                }
                if (holder.getPlayPosition() != position) {//如果是其他，需要播放自己
//                    String filePath = Constant.RECORD_FILE_PATH + "/1689647006883_18032408866.amr";
//                    CallRecordInfo callRecordInfo = new CallRecordInfo();
//                    callRecordInfo.setCallrecod(filePath);
                    RecordPlayerManager.getInstance().play(holder, holder.getData().recordPath);
                    holder.setPlayPosition(position);// 记录播放位置
                } else {// 如果是自己，只暂停，不播放；此时无播放
                    RecordPlayerManager.getInstance().clearLast();
                    holder.ivPlay.setImageResource(R.drawable.ic_play);
                    holder.rlProgress.setVisibility(View.GONE);
                    holder.tvPlayProgressTime.setText("00:00");
                    holder.setPlayPosition(-1);
                }
            }
        });
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
