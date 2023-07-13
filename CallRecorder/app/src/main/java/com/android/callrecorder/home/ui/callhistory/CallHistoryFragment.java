package com.android.callrecorder.home.ui.callhistory;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.android.callrecorder.databinding.FragmentCalllogBinding;

public class CallHistoryFragment extends Fragment {

    private FragmentCalllogBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        CallHistoryViewModel dashboardViewModel =
                new ViewModelProvider(this).get(CallHistoryViewModel.class);

        binding = FragmentCalllogBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}