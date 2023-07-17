/*Copyright ©2015 TommyLemon(https://github.com/TommyLemon)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.*/

package com.android.callrecorder.home.ui.callrecord;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.android.callrecorder.R;


/** 使用方法：复制>粘贴>改名>改代码 */

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


	private void initView() {
		ivCallType = itemView.findViewById(R.id.ic_call);
		ivPlay = itemView.findViewById(R.id.ic_play);
		tvPhoneNum = itemView.findViewById(R.id.tv_phone_num);
		tvCallTime = itemView.findViewById(R.id.tv_call_time);
		rlContent = itemView.findViewById(R.id.rl_content);
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
