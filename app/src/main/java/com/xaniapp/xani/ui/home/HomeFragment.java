package com.xaniapp.xani.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.xaniapp.xani.R;
import com.xaniapp.xani.business.ApiBusiness;
import com.xaniapp.xani.dataaccess.AppDatabase;
import com.xaniapp.xani.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

     //   final TextView textView = binding.textHome;
     //   homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    //@Override
    public void onViewCreated (View view, Bundle savedInstanceState) {

       var btn_login  = (Button)view.findViewById(R.id.btn_login);
        btn_login.setOnClickListener((x)-> processLogin());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void processLogin() {

        ApiBusiness.aquireFeed(getContext(), result -> {

            var appDatabase = AppDatabase.getDatabase(getContext());
            var userDao = appDatabase.userDao();
            var postDao = appDatabase.postDao();

            var allPosts = postDao.getAll();
            var allUsers = userDao.getAll();
        });
    }
}