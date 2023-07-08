package com.example.myapplication.ui.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.JsonReader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.toolbox.ImageRequest;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.budiyev.android.codescanner.ScanMode;
import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentHomeBinding;
import com.example.myapplication.ui.FragmentChangeListener;
import com.google.zxing.Result;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;


import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;

public class HomeFragment extends Fragment implements FragmentChangeListener {
    private CodeScanner mCodeScanner;
    private FragmentHomeBinding binding;
    private TextView scannedTextView;
    private CodeScannerView scannerView;
    private TextView productNameView;
    private ImageView productImageView;
    private Button returnToScanButton;
    private Button addToListButton;

    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        this.scannerView = root.findViewById(R.id.scanner_view);
        this.scannedTextView = root.findViewById(R.id.scanned_text);
        this.productNameView = root.findViewById(R.id.product_name);
        this.productImageView = root.findViewById(R.id.product_image);

        this.returnToScanButton = root.findViewById(R.id.return_to_scan_button);



        this.returnToScanButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                restartFragment();
                // Code here executes on main thread after user presses button
            }
        });

        this.addToListButton = root.findViewById(R.id.add_to_list_button);
        //homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
/***YEY***/

        
        final Activity activity = getActivity();


        ActivityResultLauncher<String> cameraPermission=registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if(result){
                    //Toast.makeText(activity, "Camera permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    //Toast.makeText(activity, "Camera permission not granted", Toast.LENGTH_SHORT).show();
                }
            }
        });
        cameraPermission.launch(Manifest.permission.CAMERA);

        mCodeScanner = new CodeScanner(activity, scannerView);
        mCodeScanner.setScanMode(ScanMode.SINGLE);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Toast.makeText(activity, result.getText(), Toast.LENGTH_SHORT).show();
                        scannedTextView.setText(result.getText());
                        ApiCallVolley apiCallVolley = new ApiCallVolley(result.getText(), activity);
                    }
                });
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });

        return root;
    }


    @Override
    public void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    public void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void restartFragment(){
        replaceFragment(new HomeFragment());
    }


    @Override
    public void replaceFragment(Fragment fragment) {
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.nav_host_fragment_activity_main,fragment, fragment.toString());
            //fragmentTransaction.addToBackStack(fragment.toString());
            fragmentTransaction.commit();
    }

    private class ApiCallVolley {
        private RequestQueue mRequestQueue;
        private StringRequest mStringRequest;
        private String url = "https://world.openfoodfacts.org/api/v0/product/";
        private Activity activity;

        public ApiCallVolley(String barcode, Activity activity) {
            url += barcode + ".json";
            this.activity = activity;
            getData(barcode);
        }

        private void getData(String barcode) {
            // RequestQueue initialized
            mRequestQueue = Volley.newRequestQueue(activity.getApplicationContext());

            // String Request initialized
            mStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    //Toast.makeText(activity.getApplicationContext(), "Response :" + response.toString(), Toast.LENGTH_LONG).show();//display the response on screen
                    scannedTextView.setVisibility(View.GONE);
                    scannerView.setVisibility(View.GONE);

                    productImageView.setVisibility(View.VISIBLE);
                    productNameView.setVisibility(View.VISIBLE);
                    productNameView.setText(response.toString());

                    returnToScanButton.setVisibility(View.VISIBLE);
                    addToListButton.setVisibility(View.VISIBLE);

                    try {
                        JSONObject jsonObject = new JSONObject(response.toString());
                        JSONObject productInfos = new JSONObject(jsonObject.getString("product"));
                        Picasso.get().load((String) productInfos.get("image_front_url")).into(productImageView);

                        productNameView.setText((String) productInfos.get("product_name"));
                        productNameView.setVisibility(View.VISIBLE);

                        addToListButton.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                saveProduct(barcode);
                                restartFragment();
                            }
                        });
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //Log.i(TAG, "Error :" + error.toString());
                }
            });
            mRequestQueue.add(mStringRequest);
        }

        private void saveProduct(String productCode){
            try (FileOutputStream fos = getContext().openFileOutput("saved_products", getContext().MODE_APPEND)) {
                byte[] data = StandardCharsets.UTF_8.encode((String) (productCode + "\n")).array();
                fos.write(data);
                //Toast.makeText(activity.getApplicationContext(), "datawritten", Toast.LENGTH_LONG).show();
                fos.flush();
            } catch(NullPointerException e){
                Toast.makeText(activity.getApplicationContext(), "ERROR nullpo", Toast.LENGTH_LONG).show();//display the response on screen
            } catch (IOException e) {
                //throw new RuntimeException(e);
                System.out.println(e.toString());
            }
        }
    }
}