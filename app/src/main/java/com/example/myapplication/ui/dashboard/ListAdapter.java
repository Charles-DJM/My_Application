package com.example.myapplication.ui.dashboard;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapplication.R;
import com.example.myapplication.ui.Rating;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ListAdapter extends ArrayAdapter<Product_item> {
    private Context context;
    private ArrayList<Product_item> list;

    public ListAdapter(Context context, ArrayList<Product_item> product_itemArrayList){
        //super(context, R.layout.list_item, product_itemArrayList); //R un peu chelou
        super(context, 0, product_itemArrayList);
        this.context = context;
        this.list = product_itemArrayList;

        //super(context, R.layout.list_item,  R.id.product_name,product_itemArrayList); nul
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Product_item product_item = getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        ImageView imageView = convertView.findViewById(R.id.product_image);
        TextView product_name = convertView.findViewById(R.id.product_name);
        ImageView imageRating = convertView.findViewById(R.id.product_image_rating);

        Picasso.get().load(product_item.product_image_link).into(imageView);

        product_name.setText(product_item.product_name);
        //rating.setText(product_item.rating);
        if(product_item.rating.equals(Rating.PROPRE) ){
            imageRating.setImageResource(R.drawable.ic_propre);
        }

        if(product_item.rating.equals(Rating.IMPROPRE) ){
            imageRating.setImageResource(R.drawable.ic_impropre);
            imageRating.setRotation(45);
        }

        if(product_item.rating.equals(Rating.CONSEILLE) ){
            imageRating.setImageResource(R.drawable.ic_conseille);
        }

        if(product_item.rating.equals(Rating.DECONSEILLE) ){
            imageRating.setImageResource(R.drawable.ic_deconseille);
        }
        //FAIRE IMAGE

        //return super.getView(position, convertView, parent);
        return convertView;
    }
}
