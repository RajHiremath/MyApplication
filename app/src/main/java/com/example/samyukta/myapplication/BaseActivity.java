package com.example.samyukta.myapplication;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class BaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
    }

    protected  void saveValue(String key, String phone, String msg){
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key+"_phone", phone);
        editor.putString(key+"_msg",msg );
    }

    protected  String getPhoneNumber(String key){
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        return pref.getString(key+"_phone", null);
    }

    protected  String getMessage(String key){
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        return pref.getString(key+"_msg", null);
    }

    protected  String getNextId(){
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        int id = pref.getInt("id", 0);
        if(id <9)
            editor.putInt("id", id+1);
        else
            editor.putInt("id", 0);
        return Integer.toString(id);
    }

    protected  int getCurrentId(){
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        return pref.getInt("id", 0);
    }


}
