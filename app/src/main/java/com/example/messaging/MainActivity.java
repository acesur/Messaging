package com.example.messaging;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent i = new Intent(android.content.Intent.ACTION_VIEW);
        i.putExtra("address","5556; 5558; 5560");
        i.putExtra("sms_body","Greetings!");
        i.setType("vnd.android-dir/mms-sms");
        startActivity(i);
    }
}
