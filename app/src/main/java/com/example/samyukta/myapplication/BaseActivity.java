package com.example.samyukta.myapplication;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class BaseActivity extends Activity {
    private SharedPreferences pref ;
    private SharedPreferences.Editor editor ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = pref.edit();
    }

    protected  void saveValue(int key, String address, String phone, String msg){
        editor.putString(key+"_address", address);
        editor.putString(key+"_phone", phone);
        editor.putString(key+"_msg",msg );
        editor.commit();
    }

    protected  void removeKey(int key){
        editor.remove(key+"_phone");
        editor.remove(key+"_msg");
        editor.remove(key+"_address");
        editor.commit();
    }

    protected  String getPhoneNumber(int key){
        return pref.getString(key+"_phone", null);
    }

    protected  String getMessage(int key){
        return pref.getString(key+"_msg", null);
    }

    protected  String getAddress(int key){
        return pref.getString(key+"_address", null);
    }

    protected  int getNextId(){

        int id = pref.getInt("id", 0);
        if(id <9)
            editor.putInt("id", id+1);
        else
            editor.putInt("id", 0);
        editor.commit();
        return id;
    }

    protected  int getCurrentId(){
        return pref.getInt("id", 0);
    }


}
