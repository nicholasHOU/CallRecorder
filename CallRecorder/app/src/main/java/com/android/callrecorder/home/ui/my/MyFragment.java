package com.android.callrecorder.home.ui.my;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.android.callrecorder.R;
import com.android.callrecorder.databinding.FragmentMyBinding;

public class MyFragment extends Fragment implements View.OnClickListener {

    private FragmentMyBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MyViewModel viewModel = new ViewModelProvider(this).get(MyViewModel.class);

        binding = FragmentMyBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        initView();
        return root;
    }

    private void initView() {
        binding.rlClearRecord.setOnClickListener(this);
        binding.rlFeedback.setOnClickListener(this);
        binding.rlUploadRecord.setOnClickListener(this);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_feedback:
                //jump Feedback页面
                break;
            case R.id.rl_clear_record:
                //clear path
                break;
            case R.id.rl_upload_record:
                //upload record

                break;
            default:
                break;
        }

    }
}