package com.example.myapplication.ui.dashboard;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.ui.ProductReview;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

public class Product_item {
    public String product_name, product_image_link, rating, id;

    RequestQueue requestQueue;

    public Product_item(RequestQueue requestQueue, String barcode) {
        this.requestQueue = requestQueue;
        getData(barcode);
    }

    private void getData(String barcode){
        String url = "https://world.openfoodfacts.org/api/v0/product/" + barcode.trim() + ".json";
        StringRequest sr = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject productInfos = new JSONObject(jsonObject.getString("product"));

                    product_name = (String) productInfos.get("product_name");
                    product_image_link = (String) productInfos.get("image_front_url");
                    rating = ProductReview.categorieFromProductJson(response).toString();
                    id = "1";

                }catch (Exception e){
                    System.out.println(e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                product_name = "Error";
                product_image_link = "Error";
                rating = "";
            }
        });
        requestQueue.add(sr);
    }
}
