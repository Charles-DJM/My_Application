package com.example.myapplication.ui;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public interface FragmentChangeListener {
    public void replaceFragment(Fragment fragment);

    @NonNull
    View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent);
}
