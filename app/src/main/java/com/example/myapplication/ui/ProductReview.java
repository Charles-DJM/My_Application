package com.example.myapplication.ui;

import org.json.JSONObject;

public class ProductReview {
    public static Rating categorieFromProductJson(String productInfos){
        //((JSONObject) productInfos.get("ingredients_hierarchy")).getString("en:alcohol");
        /*try{
            productInfos.getString("alcohol");
        }catch (Exception e){
            System.out.println(e);
        }*/

        if(productInfos.toString().contains("alcohol")) {
            System.out.println("LOLOLOL");
            return Rating.DECONSEILLE;
        } else {
            System.out.println("NO alcohol");
        }

        if(productInfos.toString().contains("aspartame")) {
            System.out.println("Aspartame");
            return Rating.IMPROPRE;
        }


        return Rating.PROPRE;
    }
}
