package com.example.messaging;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObservable;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends Activity {
    ContentResolver contentResolver;
    ContentObserver smsContentObserver;
    String SENT = "SMS_SENT";
    String DELIVERED = "SMS_DELIVERED";
    PendingIntent sentPI, deliveredPI;
    BroadcastReceiver smsSentReceiver, smsDeliveredReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        contentResolver = getContentResolver();



        Intent i = new Intent(android.content.Intent.ACTION_VIEW);
        i.putExtra("address","5556; 5558; 5560");
        i.putExtra("sms_body","Greetings!");
        i.setType("vnd.android-dir/mms-sms");
        startActivity(i);

        sentPI = PendingIntent.getBroadcast(this,0,new Intent(SENT),0);
        deliveredPI = PendingIntent.getBroadcast(this, 0,new Intent(DELIVERED),0);
    }

    @Override
    public void onResume(){
        super.onResume();

        //---create the BroadcastReceiver when the SMS is sent---
        smsSentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()){
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(),"SMS sent",Toast.LENGTH_SHORT).show();
                        break;
                        case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                            Toast.makeText(getBaseContext(),"Generic Failure",Toast.LENGTH_SHORT).show();
                            break;

                    case SmsManager
                                .RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(),"Generic failure",Toast.LENGTH_SHORT).show();
                    break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(),"NULL PDU",Toast.LENGTH_SHORT).show();
                        break;
                        case SmsManager.RESULT_ERROR_RADIO_OFF:
                            Toast.makeText(getBaseContext(),"Radio off",Toast.LENGTH_SHORT).show();
                            break;
                }
            }
        };


        @Override
                public void onResume(){
            super.onResume();
            smsContentObserver = new ContentObserver( new Handler()) {
            @Override
                public void onChange(boolean selfChange){
                Uri smsURI = Uri.parse("content://sms/sent");
                Cursor c = getContentResolver().query(smsURI,new String[]{"address","date","body","type"},null,null,null);

                String[] columns = new String[]{
                        "address","date","body","type"
                };

                c.moveToNext();
            String recipient = c.getString(c.getColumnIndex(columns[0]));
            String date = c.getString(c.getColumnIndex(columns[1]));
            String message = c.getString(c.getColumnIndex(columns[2]));
            String type = c.getString(c.getColumnIndex(columns[3]));

                Log.d("DetectOutgoingSMS",recipient + "," + date + ","+ message +","+type);
            }

            @Override
                public boolean deliverSelfNotifications(){
                return true;
            }

            };

            contentResolver.registerContentObserver(Uri.parse("content://sms"),true,smsContentObserver);
        }

        //--- create the BroadcastReceiver when the SMS is delivered---
        smsDeliveredReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()){
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(),"SMS delivered",Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(),"SMS not delivered",Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
        //---register the two BroadcastReceiver---
        registerReceiver(smsSentReceiver,new IntentFilter(SENT));
        registerReceiver(smsDeliveredReceiver,new IntentFilter(DELIVERED));

        //---send a SMS message---
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage("5556",null,"Greetings!",sentPI,deliveredPI);
    }

    @Override
    public void onPause(){
        super.onPause();
        //---unregister the two BroadcastReceivers---
        unregisterReceiver(smsSentReceiver);
        unregisterReceiver(smsDeliveredReceiver);
    }
}
