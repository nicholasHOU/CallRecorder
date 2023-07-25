package com.android.callrecorder.home.ui.callrecord;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.android.callrecorder.R;
import com.android.callrecorder.bean.CallRecordInfo;
import com.android.callrecorder.manager.RecordPlayerManager;

/**
 *
 */
public class CallRecordViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
	private static final String TAG = "DemoView";

	public CallRecordViewHolder(Activity context, ViewGroup parent) {
		super(context.getLayoutInflater().inflate(R.layout.item_call_record, parent,false));
		initView();
	}

	public View rlContent;
	public ImageView ivCallType;
	public ImageView ivPlay;
	public TextView tvPhoneNum;
	public TextView tvCallTime;
	public TextView tvPlayTotalTime;
	public TextView tvPlayProgressTime;
	public View rlProgress;
	public SeekBar lProgress;

	private void initView() {
		ivCallType = itemView.findViewById(R.id.ic_call);
		ivPlay = itemView.findViewById(R.id.ic_play);
		tvPhoneNum = itemView.findViewById(R.id.tv_phone_num);
		tvCallTime = itemView.findViewById(R.id.tv_call_time);
		rlContent = itemView.findViewById(R.id.rl_content);

		rlProgress = itemView.findViewById(R.id.rl_progress);
		tvPlayProgressTime = itemView.findViewById(R.id.tv_progress_time);
		tvPlayTotalTime = itemView.findViewById(R.id.tv_total_time);
		lProgress = itemView.findViewById(R.id.time_progress);
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ic_play:
			CallRecordInfo callRecordInfo = new CallRecordInfo();
			callRecordInfo.setCallrecod("");
			RecordPlayerManager.getInstance().play(this,callRecordInfo);
			break;
		default:
			break;
		}
	}

}
