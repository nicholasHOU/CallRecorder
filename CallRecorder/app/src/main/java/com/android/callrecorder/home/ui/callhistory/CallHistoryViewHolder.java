package com.android.callrecorder.home.ui.callhistory;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.android.callrecorder.R;

/**
 *
 */
public class CallHistoryViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
	private static final String TAG = "DemoView";


	public CallHistoryViewHolder(Activity context, ViewGroup parent) {
		super(context.getLayoutInflater().inflate(R.layout.item_call_history, parent,false));
		initView();
	}

	public View rlContent;
	public TextView tvCallDay;
	public TextView tvCallTimes;
	public TextView tvCallTimeDuring;


	private void initView() {
		rlContent = itemView.findViewById(R.id.rl_content);
		tvCallDay = itemView.findViewById(R.id.tv_call_date);
		tvCallTimes = itemView.findViewById(R.id.tv_call_record);
		tvCallTimeDuring = itemView.findViewById(R.id.tv_call_time);
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_content:
			break;
		default:
			break;
		}
	}

}
