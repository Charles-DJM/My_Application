package com.example.myapplication.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.databinding.FragmentDashboardBinding;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.lang.reflect.Executable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ArrayList<String> products_array = new ArrayList<>();

        try {
            FileInputStream fis = getContext().openFileInput("saved_products");
            InputStreamReader inputStreamReader = new InputStreamReader(fis, StandardCharsets.UTF_8);
            StringBuilder stringBuilder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
                String line = reader.readLine();
                while (line != null) {
                    System.out.println("LINE");
                    products_array.add(line) ;
                    line = reader.readLine();
                }
            }catch(Exception e){
                System.out.println(e.toString());
            } finally {
                //Toast.makeText(getContext(), products_string, Toast.LENGTH_LONG).show();
            }
        } catch (NullPointerException e)
        {
            System.out.println(e.toString());
        } catch (FileNotFoundException e) {
            System.out.println(e.toString());
        }

        ArrayList<Product_item> productItemArrayList = new ArrayList<>();
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());


        for (String product_code : products_array) {
            productItemArrayList.add(productFromCode(requestQueue, product_code));
        }
        Collections.reverse(productItemArrayList);
        productItemArrayList.remove(0); //Bug chelou un truc en trop
        ListAdapter listAdapter = new ListAdapter(getContext(), productItemArrayList);

        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<String>() {
            @Override
            public void onRequestFinished(Request<String> request) {
                binding.listview.setAdapter(listAdapter);
            }
        });

        return root;
    }

    private Product_item productFromCode(RequestQueue requestQueue,String code){
        return new Product_item(requestQueue,code);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}